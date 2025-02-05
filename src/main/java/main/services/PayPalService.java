package main.services;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import main.exception.OrderNotPayedException;
import main.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import main.services.utility;
import main.services.BookingService;

@Service
public class PayPalService {

    private final String returnUlr;
    private final String cancelUrl;
    private final OrderService orderService;
    private final CompanyPlatformService companyService;
    private final long AVAILABLE_TIME = 10000 * 6;
    private static final Logger log = LoggerFactory.getLogger(PayPalService.class.getName());
    private static String iban = "ROSKY1";
    private final FlightService flightService;
    private final BookingService bookingService;

    @Autowired
    public PayPalService(@Value("http://localhost:8081/payment/success") String returnUlr,
                         @Value("http://localhost:8081/payment/cancel") String cancelUrl,
                         OrderService orderService,
                         CompanyPlatformService companyService,
                         FlightService flightService,
                         BookingService bookingService) {
        this.returnUlr = returnUlr;
        this.cancelUrl = cancelUrl;
        this.orderService = orderService;
        this.companyService = companyService;
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    public PaymentOrder createPayment(Double payAmount, String iban, String flightId, String bookingReference) {
        log.info("Creating payment for amount: {}, IBAN: {}, Flight ID: {}, Booking Reference: {}", payAmount, iban, flightId, bookingReference);

        SecretEntity companyPlatform = companyService.findByIban(iban);
        if (companyPlatform != null) {
            String decryptedClientId = utility.decrypt(companyPlatform.getClientId());
            String decryptedClientSecret = utility.decrypt(companyPlatform.getClientSecret());

            log.info("Using PayPal Client ID: {}", decryptedClientId);
            log.info("Using PayPal Client Secret: {}", decryptedClientSecret);

            PayPalEnvironment environment = new PayPalEnvironment.Sandbox(decryptedClientId, decryptedClientSecret);
            PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);
        } else {
            log.error("Company platform not found for IBAN: {}", iban);
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setStatus("Error");
            return paymentOrder;
        }

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

        if (companyPlatform == null) {
            log.info("Could not find company platform for IBAN: {}", iban);
            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setStatus("Error");
            return paymentOrder;
        }

        try {
            PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
                utility.decrypt(companyPlatform.getClientId()),
                utility.decrypt(companyPlatform.getClientSecret())
            );
            PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = orderHttpResponse.result();

            log.info("PayPal order created: {}", order);

            String redirectUrl = order.links().stream()
                    .filter(link -> link.rel().equals("approve"))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("Approval URL not found in PayPal order response");
                        return new NoSuchElementException("Approval URL not found");
                    })
                    .href();

            log.info("Approval URL: {}", redirectUrl);

            OrderEntity orderStatus = new OrderEntity();
            orderStatus.setOrderId(order.id());
            
            orderStatus.setStatus("INITIATED");
            orderStatus.setIban(iban);
            orderStatus.setFlight(flightService.findFlightById(Long.parseLong(flightId)));
            orderStatus.setBookingReference(bookingReference);
            orderStatus.setCreationTime(System.currentTimeMillis());
            orderStatus.setExpirationTime(orderStatus.getCreationTime() + AVAILABLE_TIME);

            log.info("Order entity created: {}", orderStatus);

            orderService.addOrder(orderStatus);

            // -----
            BookingEntity booking = bookingService.findByReference(orderStatus.getBookingReference());
            if(booking != null){
                booking.setStatus("INITIATED");
                bookingService.save(booking);
            }

            return new PaymentOrder("success", order.id(), redirectUrl);
        } catch (IOException e) {
            log.error("Error during PayPal environment setup: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to set up PayPal environment", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }

    public GetOrder getOrder(String orderId, String iban) {
        OrdersGetRequest ordersGetRequest = new OrdersGetRequest(orderId);
        SecretEntity companyPlatform = companyService.findByIban(iban);

        if (companyPlatform == null) {
            throw new RuntimeException("Company not found for IBAN: " + iban);
        }

        try {
            PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
                utility.decrypt(companyPlatform.getClientId()),
                utility.decrypt(companyPlatform.getClientSecret())
            );
            PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);

            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersGetRequest);
            Order order = httpResponse.result();
            GetOrder getOrderObj = new GetOrder();
            getOrderObj.setPayee(order.purchaseUnits().get(0).payee());
            getOrderObj.setPayer(order.payer());

            getOrderObj.setPayerId(getOrderObj.getPayer().payerId());
            getOrderObj.setPayerEmail(getOrderObj.getPayer().email());

            return getOrderObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public CompletedOrder captureOrder(String token, String payerID, String iban) {
        log.info("Capturing order with token: {}, payerID: {}, IBAN: {}", token, payerID, iban);
    
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        SecretEntity companyPlatform = companyService.findByIban(iban);
        OrderEntity orderStatus = orderService.findByOrderId(token);
        long callTime = System.currentTimeMillis();
    
        if (orderStatus == null) {
            throw new RuntimeException("Order not found for token: " + token);
        }
    
        if (companyPlatform == null) {
            throw new RuntimeException("Company not found for IBAN: " + iban);
        }
    
        if (callTime > orderStatus.getExpirationTime() && !orderStatus.getStatus().equals("SUCCESS")) {
            orderStatus.setStatus("CANCELED");
            // -----
            BookingEntity booking = bookingService.findByReference(orderStatus.getBookingReference());
            if(booking != null){
                booking.setStatus("CANCELED");
                bookingService.save(booking);
            }
            orderService.updateOrder(orderStatus, token).block();
            return new CompletedOrder(orderStatus.getStatus(), String.valueOf(orderStatus.getId()));
        } else {
            try {
                String decryptedClientId = utility.decrypt(companyPlatform.getClientId());
                String decryptedClientSecret = utility.decrypt(companyPlatform.getClientSecret());

                PayPalEnvironment environment = new PayPalEnvironment.Sandbox(decryptedClientId, decryptedClientSecret);
                PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);
    
                HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
                Order order = httpResponse.result();

                if(!order.payer().payerId().equals(payerID)){
                    throw new RuntimeException("Payer ID does not match");
                }
    
                log.info("Order captured: {}", order);
    
                orderStatus.setStatus("SUCCESS");
                OrderEntity updatedOrderStatus = orderService.updateOrder(orderStatus, orderStatus.getOrderId()).block();
                // -----
                BookingEntity booking = bookingService.findByReference(orderStatus.getBookingReference());
                if(booking != null){
                    booking.setStatus("CONFIRMED");
                    bookingService.save(booking);
                }

                return new CompletedOrder(updatedOrderStatus.getStatus(), String.valueOf(updatedOrderStatus.getId()));
            } catch (Exception ex) {
                log.error("Error capturing order: {}", ex.getMessage(), ex);
                if (ex.getMessage().contains("ORDER_ALREADY_CAPTURED")) {
                    log.info("Order already captured: {}", token);
                    return new CompletedOrder("SUCCESS", String.valueOf(orderStatus.getId()));
                } else if (ex.getMessage().contains("ORDER_NOT_APPROVED")) {
                    orderStatus.setStatus("CANCELED");
                    OrderEntity updatedOrder = orderService.updateOrder(orderStatus, token).block();
                    return new CompletedOrder(updatedOrder.getStatus(), String.valueOf(updatedOrder.getId()));
                } else {
                    throw new OrderNotPayedException("Error processing payment: " + ex.getMessage());
                }
            }
        }
    }

    public String getCancelUrl() {
        return cancelUrl;
    }
}
