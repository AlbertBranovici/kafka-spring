package main.services;

import main.models.BookingEntity;
import main.models.OrderStatus;
import main.repositories.BookingRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository repo;
    private final FlightService flightService;
    private final KafkaProducerService producer;

    @Autowired
    public BookingService(BookingRepository repo, FlightService flightService, KafkaProducerService producer) {
        this.repo = repo;
        this.flightService = flightService;
        this.producer = producer;
    }

    public BookingEntity save(BookingEntity bookingEntity) {
        BookingEntity savedBooking = repo.save(bookingEntity);
        producer.sendMessage("bookings",savedBooking);
        return savedBooking;
//        aici de adaugat kafka
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

    public void deleteByReference(String reference) {
        repo.deleteBookingEntityByBookingReference(reference);
    }
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @KafkaListener(topics = "payments", groupId = "test_group")
    public BookingEntity listenToKafkaTopic(ConsumerRecord<String, OrderStatus> record){
        OrderStatus order = record.value();
        String bookingReference = order.getBookingReference();
        log.info("Order status : {}", order.toString());
        Optional<BookingEntity> existingBooking = repo.findByBookingReference(bookingReference);
        if(order.getStatus().equals("SUCCESS")){


            if(existingBooking.isPresent()){
                BookingEntity bookingEntity = existingBooking.get();
                bookingEntity.setStatus("CONFIRMED");
                flightService.updateSeats(bookingEntity.getFlight().getIdflights(), bookingEntity.getSeats());

                return repo.save(bookingEntity);
            } else {
                throw new RuntimeException("Booking not found with ref: " + bookingReference);
            }
        } else {
            if(existingBooking.isPresent()){
                BookingEntity bookingEntity = existingBooking.get();
                bookingEntity.setStatus("CANCELED");

                return repo.save(bookingEntity);
            } else {
                throw new RuntimeException("Booking not found with ref: " + bookingReference);
            }

        }
    }
}
