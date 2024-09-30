package main.services;

import main.models.dto.FlightDTO;
import main.models.FlightEntity;
import main.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class FlightService {

    private final FlightRepository repo;

    @Autowired
    public FlightService(FlightRepository repo) {
        this.repo = repo;
    }

    public List<FlightEntity> getAllFlights() {
        return repo.findAll();
    }

    public List<FlightEntity> getFlightByNumber(String flightNumber) {
        return repo.findByFlightNumber(flightNumber);
    }

    public Optional<FlightEntity> getFlightById(Long idflights){
        return repo.findByIdflights(idflights);
    }

    public void updateSeats(Long idflights, int seats) {
        Optional<FlightEntity> flightOptional = repo.findByIdflights(idflights);
        if (flightOptional.isPresent()) {
            FlightEntity flightEntity = flightOptional.get();
            flightEntity.setTotalSeats(flightEntity.getTotalSeats() - seats);
            repo.save(flightEntity);
        }
    }

    public void deleteById(Long idflights){
        repo.deleteById(idflights);
    }

    public List<FlightEntity> getFlightByArrival(String arrival){
        return repo.findByArrivalLocation(arrival);
    }
    public void saveFlight(FlightDTO dto) {
        FlightEntity f = new FlightEntity();

        if (dto.getCodeshared().getFlight().getNumber() != null) {
            f.setFlightNumber(dto.getCodeshared().getFlight().getNumber());
        } else {
            f.setFlightNumber(null); // Set to null if the flight number is missing
        }
        f.setCompany(dto.getAirline().getName());

        //Setting random price
        Random random = new Random();
        int price = random.nextInt(100,500);
        f.setPrice(price);

        int terminal = random.nextInt(1,4);
        f.setTerminal(Integer.toString(terminal));

        f.setArrivalLocation(dto.getArrival().getIataCode());
//        f.setTerminal(dto.getDeparture().getTerminal());
        f.setDepartureLocation("OTP");
        f.setArrivalLocation(dto.getArrival().getIataCode());
        f.setDepartureTime(dto.getDeparture().getScheduledTime());

        repo.save(f);
    }

}
