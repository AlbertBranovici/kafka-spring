package main.repositories;

import main.models.OrderStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<OrderStatus, String> {
    Mono<OrderStatus> findByOrderId(String orderId);
}
