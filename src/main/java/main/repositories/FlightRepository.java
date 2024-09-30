package main.repositories;

import main.models.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, Long> {
    List<FlightEntity> findByArrivalLocation(String arrivalLocation);
    List<FlightEntity> findByFlightNumber(String flightNumber);
    Optional<FlightEntity> findByIdflights(Long idflights);
}
