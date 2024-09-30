package main.controller;

import com.paypal.api.payments.*;
import com.paypal.base.rest.PayPalRESTException;
import main.models.OrderStatus;
import main.services.OrderService;
import main.services.PayPalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;


@Controller
public class HomeController {
    private final PayPalService payPalService;
    private final OrderService orderService;


    public HomeController(PayPalService payPalService, OrderService orderService) {
        this.payPalService = payPalService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
    @GetMapping("/payment/cancel")
    public String cancelPayment(@RequestParam("token") String token){
        Mono<OrderStatus> orderMono = orderService.findByOrderId(token);
        orderMono.subscribe(event -> {
            payPalService.captureOrder(event.getOrderId(), event.getIban());
        });
        return "cancel";
    }

    @GetMapping("/payment/success")
    public String successPayment(@RequestParam("token") String token, @RequestParam("PayerID") String payerId) {
        Mono<OrderStatus> orderMono = orderService.findByOrderId(token);
        orderMono.subscribe(event -> {
            payPalService.captureOrder(event.getOrderId(), event.getIban());
        });
        return "success";
    }
//AICI DE COMPLETAT

//    @GetMapping("payment/success")
//    public String successPayment(ServerHttpRequest serverHttpRequest) {
//        MultiValueMap<String, String> queryParams = serverHttpRequest.getQueryParams();
//        String token = queryParams.getFirst("token");
//
//        Mono<OrderStatus> orderMono = orderService.findByOrderId(token);
//        orderMono.subscribe(event -> {
//            payPalService.captureOrder(event.getOrderId(), event.getIban()).subscribe();
//        });
//        return "success";
//    }




}
