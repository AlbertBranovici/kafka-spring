package main.services;

import main.models.OrderStatus;
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

    public Mono<OrderStatus> addOrder(OrderStatus orderStatus) { return orderRepository.save(orderStatus); }

    public Mono<OrderStatus> findByOrderId(String orderId) { return orderRepository.findByOrderId(orderId); }

    public Mono<OrderStatus> updateOrder(OrderStatus orderStatus, String orderId){
        Mono<OrderStatus> existingOrderStatus = orderRepository.findByOrderId(orderId);
        return existingOrderStatus.flatMap(event ->{
           event.setStatus(orderStatus.getStatus());
           return orderRepository.save(event);
        });
    }



}
