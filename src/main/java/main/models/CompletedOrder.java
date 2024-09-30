package main.models;

public class CompletedOrder {
    private String status;
    private String payId;

    public CompletedOrder() {
    }

    public CompletedOrder(String status, String payId) {
        this.status = status;
        this.payId = payId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }
}
