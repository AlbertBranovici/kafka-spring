package main.controller;

import main.models.FlightEntity;
import main.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/flight")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }
    //  http://localhost:8081/api/v1/flight
    @GetMapping
    public ResponseEntity<List<FlightEntity>> getFlights() {
        List<FlightEntity> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    // http://localhost:8081/api/v1/flight/
    @GetMapping("/{flightNumber}")
    public ResponseEntity<List<FlightEntity>> getNumberOfFlight(@PathVariable("flightNumber") String number) {
        List<FlightEntity> flights = flightService.getFlightByNumber(number);

        if (flights != null && !flights.isEmpty()) {
            return ResponseEntity.ok(flights);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // http://localhost:8081/api/v1/flight/id/
    @GetMapping("/id/{id}")
    public ResponseEntity<FlightEntity> getFlightById(@PathVariable("id") Long id) {
        Optional<FlightEntity> flight = flightService.getFlightById(id);
        if(flight.isPresent()) {
            return ResponseEntity.ok(flight.get());
        } else{
            return ResponseEntity.notFound().build();
        }
    }

    // http://localhost:8081/api/v1/flight/destination/
    @GetMapping("/destination/{dest}")
    public ResponseEntity<List<FlightEntity>> getFlightByDestination(@PathVariable("dest") String destination) {
        List<FlightEntity> flights = flightService.getFlightByArrival(destination);
        if (flights != null && !flights.isEmpty()) {
            return ResponseEntity.ok(flights);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // http://localhost:8081/api/v1/flight/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable("id") Long id) {
        Optional<FlightEntity> flight = flightService.getFlightById(id);
        if (flight.isPresent()){
            flightService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
