package main.repositories;

import main.models.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
//    BookingEntity findByBookingId(Long id);
    BookingEntity findBookingEntityByBookingReference(String bookingReference);
    void deleteBookingEntityByBookingReference(String bookingReference);
    Optional<BookingEntity> findByBookingReference(String bookingReference);
}
