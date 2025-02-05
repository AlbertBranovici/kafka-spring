package main.services;

import main.models.OrderEntity;
import main.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderRepository orderRepository1) {
        this.orderRepository = orderRepository1;
    }

    public OrderEntity addOrder(OrderEntity orderStatus) { return orderRepository.save(orderStatus); }

    public OrderEntity findByOrderId(String orderId) { return orderRepository.findByOrderId(orderId); }

    public Mono<OrderEntity> updateOrder(OrderEntity orderStatus, String orderId){
        Mono<OrderEntity> existingOrderStatus = Mono.justOrEmpty(orderRepository.findByOrderId(orderId));
        return existingOrderStatus.flatMap(event -> {
            event.setStatus(orderStatus.getStatus());
            return Mono.just(orderRepository.save(event));
        });
    }



}
