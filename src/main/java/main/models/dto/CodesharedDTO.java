package main.models.dto;

public class CodesharedDTO {
    private AirlineDTO airline;
    private FlightInfoDTO flight;

    // Getters and Setters

    public AirlineDTO getAirline() {
        return airline;
    }

    public void setAirline(AirlineDTO airline) {
        this.airline = airline;
    }

    public FlightInfoDTO getFlight() {
        return flight;
    }

    public void setFlight(FlightInfoDTO flight) {
        this.flight = flight;
    }
}
