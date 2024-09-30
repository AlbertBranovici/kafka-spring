package main.models.dto;

// FlightDTO.java
public class FlightDTO {
    private AirlineDTO airline;
    private ArrivalDTO arrival;



    private CodesharedDTO codeshared;
    private DepartureDTO departure;
    private String status;
    private String type;

    // Getters and Setters

    @Override
    public String toString() {
        return "FlightDTO{" +
                "airline=" + airline +
                ", arrival=" + arrival +
                ", codeshared=" + codeshared +
                ", departure=" + departure +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public AirlineDTO getAirline() {
        return airline;
    }

    public void setAirline(AirlineDTO airline) {
        this.airline = airline;
    }

    public ArrivalDTO getArrival() {
        return arrival;
    }

    public void setArrival(ArrivalDTO arrival) {
        this.arrival = arrival;
    }

    public CodesharedDTO getCodeshared() {
        return codeshared;
    }

    public void setCodeshared(CodesharedDTO codeshared) {
        this.codeshared = codeshared;
    }

    public DepartureDTO getDeparture() {
        return departure;
    }

    public void setDeparture(DepartureDTO departure) {
        this.departure = departure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

