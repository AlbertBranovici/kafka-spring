package main.controller;

import main.models.CompletedOrder;
import main.models.GetOrder;
import main.models.PaymentOrder;
import main.services.CompanyPlatformService;
import main.services.PayPalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/paypal")
public class PaymentController {

    private final PayPalService payPalService;
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class.getName());
    private final CompanyPlatformService s;

    @Autowired
    public PaymentController(PayPalService payPalService, CompanyPlatformService s) {
        this.payPalService = payPalService;
        this.s = s;
    }
    @PostMapping("/init")
    public PaymentOrder createPayment(@RequestParam(name = "sum") Double sum,
                                      @RequestParam(name = "iban") String iban,
                                                                           @RequestParam(name="flightId") String flightId,
                                                                           @RequestParam(name="bookingReference") String bookingReference){
        return payPalService.createPayment(sum, iban, flightId, bookingReference);
    }

    @PostMapping(value="/capture")
    public CompletedOrder completePayment(@RequestParam("token") String token,
                                          @RequestParam(name = "iban") String iban){
        return payPalService.captureOrder(token, iban);
    }

    @GetMapping(value = "/get")
    public GetOrder getPayment(@RequestParam("token") String token,
                               @RequestParam(name = "iban") String iban){
        return payPalService.getOrder(token, iban);
    }

    @PostMapping("/time")
    public CompletedOrder captureOrder(@RequestParam("token") String token,
                                       @RequestParam(name = "iban") String iban){
        return payPalService.captureOrder(token, iban);
    }
}
