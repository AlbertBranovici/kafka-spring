package main.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name="flights")
public class FlightEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idflights;

    @Column(name="company")
    private String company;

    @Column(name="departure_location")
    private String departureLocation;

    @Column(name="arrival_location")
    private String arrivalLocation;

    @Column(name="terminal")
    private String terminal;

    @Column(name="departure_time")
    private String departureTime;

    @Column(name="flight_number", nullable = true)
    private String flightNumber;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("flight")
    private List<BookingEntity> bookings;

    @Column(name="price")
    private int price;

    @Column(name="totalSeats")
    private int totalSeats = 300;

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public List<BookingEntity> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingEntity> bookings) {
        this.bookings = bookings;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public FlightEntity(Long idflights, String company, String departureLocation, String arrivalLocation, String terminal, String departureTime, String flightNumber, List<BookingEntity> bookings, int price, int totalSeats) {
        this.idflights = idflights;
        this.company = company;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.terminal = terminal;
        this.departureTime = departureTime;
        this.flightNumber = flightNumber;
        this.bookings = bookings;
        this.price = price;
        this.totalSeats = totalSeats;
    }

    public FlightEntity() {}

    public Long getIdflights() {
        return idflights;
    }

    public void setIdflights(Long idflights) {
        this.idflights = idflights;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
}
