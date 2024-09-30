package main.services;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.*;
import com.paypal.orders.Item;
import main.exception.OrderNotPayedException;
import main.models.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PayPalService {

    private final String returnUlr;
    private final String cancelUrl;
    private final OrderService orderService;
    private final CompanyPlatformService companyService;
    private final long AVAILABLE_TIME = 10000 * 6;
    private static final Logger log = LoggerFactory.getLogger(PayPalService.class.getName());
    private final KafkaProducerService kafkaProducer;
    private static String iban = "ROSKY1";

    @Autowired
    public PayPalService(@Value("http://localhost:8081/payment/success") String returnUlr,
                         @Value("http://localhost:8081/payment/cancel") String cancelUrl,
                         OrderService orderService,
                         CompanyPlatformService companyService, KafkaProducerService kafkaProducer) {
        this.returnUlr = returnUlr;
        this.cancelUrl = cancelUrl;
        this.orderService = orderService;
        this.companyService = companyService;
        this.kafkaProducer = kafkaProducer;
    }

    @KafkaListener(topics = "bookings", groupId = "test_group")
    public void listenToKafkaTopic(ConsumerRecord<String, BookingEntity> record){
        BookingEntity booking = record.value();

        Double total = (double) booking.getPrice();
        String flightId = String.valueOf(booking.getFlight().getIdflights());
        String bookingReference = booking.getBookingReference();
        PaymentOrder paymentOrder = createPayment(total, iban, flightId, bookingReference);
        
        if ("Error".equals(paymentOrder.getStatus())) {
            log.error("Payment error: Payment creation failed");
        } else {
            log.info("Payment order created: {}", paymentOrder.getStatus());
        }
    }


    public PaymentOrder createPayment(Double payAmount, String iban, String flightId, String bookingReference){
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown();
        amountWithBreakdown.currencyCode("EUR");
        amountWithBreakdown.value(payAmount.toString());

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest();
        purchaseUnitRequest.amountWithBreakdown(amountWithBreakdown);

        Money money = new Money();
        money.currencyCode(amountWithBreakdown.currencyCode());
        money.value(amountWithBreakdown.value());

        AmountBreakdown amountBreakdown = new AmountBreakdown();
        amountBreakdown.itemTotal(money);
        amountWithBreakdown.amountBreakdown(amountBreakdown);

        Item item = new Item();
        item.category("DIGITAL_GOODS");
        item.quantity("1");
        item.name("Flight to X");
        item.description("Flight ticket");
        item.unitAmount(money);

        purchaseUnitRequest.items(List.of(item));
        purchaseUnitRequest.amountWithBreakdown().amountBreakdown().itemTotal(money);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));

        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.returnUrl(returnUlr);
        applicationContext.cancelUrl(cancelUrl);
        applicationContext.userAction("PAY_NOW");

        orderRequest.applicationContext(applicationContext);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        CompanyPlatform companyPlatform = companyService.findByIban(iban).block();

        if (companyPlatform == null) {
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setStatus("Error");
            return paymentOrder;
        }

        try {
            PayPalEnvironment environment = new PayPalEnvironment.Sandbox(companyPlatform.getClientId(), companyPlatform.getClientSecret());
            PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
                       Order order = orderHttpResponse.result();

            String redirectUrl = order.links().stream()
                    .filter(link -> link.rel().equals("approve"))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOrderId(order.id());
            orderStatus.setStatus("INITIATED");
            orderStatus.setIban(iban);
            orderStatus.setFlightId(flightId);
            orderStatus.setBookingReference(bookingReference);
            orderStatus.setCreationTime(System.currentTimeMillis());
            orderStatus.setExpirationTime(orderStatus.getCreationTime() + AVAILABLE_TIME);
                       log.info("Order created: {}", orderStatus);

            orderService.addOrder(orderStatus).subscribe();

            PaymentOrder paymentOrder = new PaymentOrder("success", order.id(), redirectUrl);
            try {
                Runtime.getRuntime().exec("open " + redirectUrl);
            } catch (Exception e) {
                log.error("Failed to open redirect URL: {}", e.getMessage());
            }
            return paymentOrder;

        } catch (Exception e) {
            log.error("Error creating payment: {}", e.getMessage());
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setStatus("Error");
            return paymentOrder;
        }
    }

    public GetOrder getOrder(String orderId, String iban){
        OrdersGetRequest ordersGetRequest = new OrdersGetRequest(orderId);
        CompanyPlatform companyPlatform = companyService.findByIban(iban).block();

        if (companyPlatform == null) {
            throw new RuntimeException("Company not found for IBAN: " + iban);
        }

        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(companyPlatform.getClientId(), companyPlatform.getClientSecret());
        PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);

        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersGetRequest);
                       Order order = httpResponse.result();
            GetOrder getOrderObj = new GetOrder();
            getOrderObj.setPayee(order.purchaseUnits().get(0).payee());
            getOrderObj.setPayer(order.payer());

            getOrderObj.setPayerId(getOrderObj.getPayer().payerId());
            getOrderObj.setPayerEmail(getOrderObj.getPayer().email());

            return getOrderObj;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    
    }
    public CompletedOrder captureOrder(String token, String iban){
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        CompanyPlatform companyPlatform = companyService.findByIban(iban).block();
        OrderStatus orderStatus = orderService.findByOrderId(token).block();
        long callTime = System.currentTimeMillis();

        if (orderStatus == null) {
            throw new RuntimeException("Order not found for token: " + token);
        }

        if (companyPlatform == null) {
            throw new RuntimeException("Company not found for IBAN: " + iban);
        }

        if(callTime > orderStatus.getExpirationTime() && !orderStatus.getStatus().equals("SUCCESS")){
            orderStatus.setStatus("CANCELED");
            sendOrderToKafka(orderStatus);
            orderService.updateOrder(orderStatus, token).block();
            return new CompletedOrder(orderStatus.getStatus(), orderStatus.getId());
        } else {
            PayPalEnvironment environment = new PayPalEnvironment.Sandbox(companyPlatform.getClientId(), companyPlatform.getClientSecret());
            PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);
            try {
                HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
                Order order = httpResponse.result();
                orderStatus.setStatus("SUCCESS");
                OrderStatus updatedOrderStatus = orderService.updateOrder(orderStatus, orderStatus.getOrderId()).block();
                if (updatedOrderStatus.getStatus().equals("SUCCESS")) {
                    sendOrderToKafka(updatedOrderStatus);
                }
                return new CompletedOrder(updatedOrderStatus.getStatus(), updatedOrderStatus.getId());
            } catch(IOException ex) {
                if(ex.getMessage().contains("ORDER_NOT_APPROVED")){
                    orderStatus.setStatus("CANCELED");
                    sendOrderToKafka(orderStatus);
                    OrderStatus updatedOrder = orderService.updateOrder(orderStatus, token).block();
                    return new CompletedOrder(updatedOrder.getStatus(), updatedOrder.getId());
                } else {
                    throw new OrderNotPayedException("Error processing payment: " + ex.getMessage());
                }
            }
        }
    }
//    public Mono<CompletedOrder> captureOrder(String token, String iban){
//        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
//        Mono<CompanyPlatform> monoCompany = companyService.findByIban(iban);
//        Mono<OrderStatus> orderStatusMono = orderService.findByOrderId(token);
//        long callTime = System.currentTimeMillis();
//
//        Mono<CompletedOrder> resultMono = orderStatusMono.flatMap(orderStatus -> {
//            if(callTime > orderStatus.getExpirationTime() && !orderStatus.getStatus().equals("SUCCESS")){
//                orderStatus.setStatus("CANCELED");
//                orderService.updateOrder(orderStatus, token).subscribe();
//
//                return Mono.just(new CompletedOrder(orderStatus.getStatus(), orderStatus.getId()));
//            } else {
//                monoCompany.subscribe(e -> {
//                    PayPalEnvironment environment = new PayPalEnvironment.Sandbox(e.getClientId(), e.getClientSecret());
//                    PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);
//                    try{
//                        HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
//                        Order order = httpResponse.result();
//                        orderStatus.setStatus("SUCCESS");
//                        //aici de apelat functia pentru kafka de trimis mesaje
//                        orderService.updateOrder(orderStatus, orderStatus.getOrderId()).subscribe();
//                    } catch(IOException ex){
//                        throw new OrderNotPayedException("Payment with id " + token + " was not paid by following the given link");
//
//                    }
//                });
//                return Mono.just(new CompletedOrder(orderStatus.getStatus(), orderStatus.getId()));
//            }
//        });
//        return resultMono;
//    }
//
    private void sendOrderToKafka(OrderStatus orderStatus){
        kafkaProducer.sendMessage("payments",orderStatus);
    }

//
//    private final APIContext apiContext;
//
//    public Payment createPayment(Double total,
//                                 String currency,
//                                 String method,
//                                 String intent,
//                                 String description,
//                                 String cancelUrl,
//                                 String successUrl) throws PayPalRESTException {
//        Amount amount = new Amount();
//        amount.setCurrency(currency);
//        amount.setTotal(String.format(Locale.forLanguageTag(currency),"%.2f",total));
//
//        Transaction transaction = new Transaction();
//        transaction.setDescription(description);
//        transaction.setAmount(amount);
//
//        List<Transaction> transactions = new ArrayList<>();
//        transactions.add(transaction);
//
//        Payer payer = new Payer();
//        payer.setPaymentMethod(method);
//
//        Payment payment = new Payment();
//        payment.setIntent(intent);
//        payment.setPayer(payer);
//        payment.setTransactions(transactions);
//
//        RedirectUrls redirectUrls = new RedirectUrls();
//        redirectUrls.setCancelUrl(cancelUrl);
//        redirectUrls.setReturnUrl(successUrl);
//        payment.setRedirectUrls(redirectUrls);
//
//        return payment.create(apiContext);
//
//    }
//
//    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
//        Payment payment = new Payment();
//        payment.setId(paymentId);
//
//        PaymentExecution paymentExecution = new PaymentExecution();
//        paymentExecution.setPayerId(payerId);
//
//        return payment.execute(apiContext, paymentExecution);
//    }
}
