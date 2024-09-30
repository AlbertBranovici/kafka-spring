package main.models;

import com.paypal.orders.Payee;
import com.paypal.orders.Payer;

import java.io.Serializable;

public class GetOrder implements Serializable {
    Payee payee;
    String payerId;
    String payerEmail;
    String payeeId;
    String payeeEmail;
    Payer payer;

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public GetOrder(Payee payee, Payer payer) {
        this.payee = payee;
        this.payer = payer;
    }

    public GetOrder() {
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeEmail() {
        return payeeEmail;
    }

    public void setPayeeEmail(String payeeEmail) {
        this.payeeEmail = payeeEmail;
    }
}
