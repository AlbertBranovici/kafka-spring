package main.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrderStatus {
    @Id
    private String id;
    private String orderId;
    private String status;
    private Long creationTime;
    private Long expirationTime;
    private String iban;
    private String flightId;
    private String bookingReference;

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public OrderStatus(String id, String orderId, String status, Long creationTime, Long expirationTime, String iban, String flightId) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
        this.iban = iban;
        this.flightId = flightId;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public OrderStatus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "id='" + id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", status='" + status + '\'' +
                ", creationTime=" + creationTime +
                ", expirationTime=" + expirationTime +
                ", iban='" + iban + '\'' +
                ", flightId='" + flightId + '\'' +
                ", bookingReference='" + bookingReference + '\'' +
                '}';
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
