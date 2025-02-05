package main.services;

import main.models.BookingEntity;
import main.models.OrderEntity;
import main.repositories.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository repo;
    private final FlightService flightService;
    private final OrderService orderService;

    @Autowired
    public BookingService(BookingRepository repo, FlightService flightService, OrderService orderService) {
        this.repo = repo;
        this.flightService = flightService;
        this.orderService = orderService;
    }

    public BookingEntity save(BookingEntity bookingEntity) {
        return repo.save(bookingEntity);
    }

    public List<BookingEntity> findAll() {
        return repo.findAll();
    }

    public BookingEntity findByReference(String bookingReference) {
        return repo.findBookingEntityByBookingReference(bookingReference);
    }

    public Optional<BookingEntity> findById(Long id) {
        return repo.findById(id);
    }
    @Transactional
    public void deleteByReference(String reference) {
        repo.deleteBookingEntityByBookingReference(reference);
    }

    @Transactional
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public BookingEntity updateBookingStatusFromDatabase(String bookingReference) {
        log.info("Updating booking status for reference: {}", bookingReference);
        Optional<BookingEntity> existingBooking = repo.findByBookingReference(bookingReference);

        if (existingBooking.isPresent()) {
            BookingEntity bookingEntity = existingBooking.get();
            OrderEntity order = orderService.findByOrderId(bookingReference);

            if (order != null) {
                if ("SUCCESS".equals(order.getStatus())) {
                    bookingEntity.setStatus("CONFIRMED");
                    flightService.updateSeats(bookingEntity.getFlight().getIdflights(), bookingEntity.getSeats());
                } else {
                    bookingEntity.setStatus("CANCELED");
                }
                return repo.save(bookingEntity);
            } else {
                throw new RuntimeException("Order not found with ref: " + bookingReference);
            }
        } else {
            throw new RuntimeException("Booking not found with ref: " + bookingReference);
        }
    }
}
