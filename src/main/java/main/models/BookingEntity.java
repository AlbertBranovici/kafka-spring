package main.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name="bookings")
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="booking_reference", nullable = false)
    private String bookingReference;

    @Column(name="full_name")
    private String full_name;

    @ManyToOne
    @JoinColumn(name="flight_id", nullable=true)
//    @JsonBackReference
    @JsonIgnoreProperties("bookings")
    private FlightEntity flight;

    @Column(name="price")
    private Integer price;

    @Column(name="status")
    private String status;

    @Column(name="seats", nullable = false)
    private int seats = 0;

    @ManyToOne
    @JoinColumn(name = "customerID", nullable = true)
    private CustomerEntity customer;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public FlightEntity getFlight() {
        return flight;
    }

    public void setFlight(FlightEntity flight) {
        this.flight = flight;
    }
}
