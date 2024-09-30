package main.controller;
import main.models.*;
import main.services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/booking")
public class BookingController {
    private final BookingService bookingService;
    private final FlightService flightService;
    private static final Logger log = LoggerFactory.getLogger(GetFlightData.class.getName());

    @Autowired
    public BookingController(BookingService bookingService, FlightService flightService) {
        this.bookingService = bookingService;
        this.flightService = flightService;
    }
    // http://localhost:8081/api/v1/booking/add
    @PostMapping("/add")
    public ResponseEntity<?> addBooking(@RequestBody BookingEntity booking) {
        Optional<FlightEntity> flight = flightService.getFlightById(booking.getFlight().getIdflights());
        if (flight.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        booking.setFlight(flight.get());
        booking.setPrice(flight.get().getPrice() / 10 * booking.getSeats());
        if (booking.getBookingReference() == null || booking.getBookingReference().isEmpty()) {
            booking.setBookingReference(generateBookingReference());
        }
        if(booking.getSeats() > flight.get().getTotalSeats()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough seats left on the plane");
        }

        bookingService.save(booking);
        return ResponseEntity.ok(booking);

    }


    //GET
    @GetMapping
    public ResponseEntity<List<BookingEntity>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAll());
    }
    // http://localhost:8081/api/v1/booking/
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        Optional<BookingEntity> booking = bookingService.findById(id);
        if (booking.isPresent()) {
            return ResponseEntity.ok(booking.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // http://localhost:8081/api/v1/booking/ref/
    @GetMapping("/ref/{reference}")
    public ResponseEntity<?> getBookingByReference(@PathVariable String reference) {
        BookingEntity booking = bookingService.findByReference(reference);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(booking);
        }
    }


    //DELETE
    // http://localhost:8081/api/v1/booking/ref/
    @DeleteMapping("/ref/{reference}")
    public ResponseEntity<String> deleteBookingByReference(@PathVariable String reference) {
        BookingEntity booking = bookingService.findByReference(reference);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        } else {
            bookingService.deleteByReference(reference);
            return ResponseEntity.ok().build();
        }
    }

    //http://localhost:8081/api/v1/booking/
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        if(bookingService.findById(id).isPresent()) {
            bookingService.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(404).body("Booking with id "+id+"not found");
        }
    }

    private String generateBookingReference(){
        return UUID.randomUUID().toString().replace("-","").substring(0,6).toUpperCase();
    }

}
