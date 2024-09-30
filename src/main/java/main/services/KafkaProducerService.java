package main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private final String TOPIC = "rest_api";
//    private final String TOPIC = "bookings";

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String TOPIC, Object message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
//@Service
//public class KafkaProducerService {
//
//    private final KafkaTemplate<String, products> kafkaTemplate;
////    private final String TOPIC = "rest_api";
//    private final String TOPIC = "recent_changes";
//
//    @Autowired
//    public KafkaProducerService(KafkaTemplate<String, products> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendMessage(products products) {
//        kafkaTemplate.send(TOPIC, products);
//    }
//}
