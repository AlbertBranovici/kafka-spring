package main.services;

import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
//    private final static String TOPIC = "rest_api";
    private final static String TOPIC = "recent_changes";

//    private final KafkaTemplate<String, products> kafkaTemplate;
//
//    public KafkaConsumerService(KafkaTemplate<String, products> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }


//    @KafkaListener(topics = TOPIC, groupId = "test_group", containerFactory = "kafkaListenerContainerFactory")
//    public void consume(ConsumerRecord<String, products> record) {
//        String key = record.key();
//        products prod = record.value();
//        System.out.println("Consumed product: "+prod.getName());
//
//        kafkaTemplate.send("test_prods",key, prod);
//
//    }


//    @KafkaListener(topics = TOPIC, groupId = "test_group", containerFactory = "kafkaListenerContainerFactory")
//    public void consume(FlightDTO recentChange) {
//        System.out.println("Consumed product: " + recentChange.getType());
//
//    }
}
