package main.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.102:19092");
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "redpanda-0:9092"); pentru conduktor
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092"); //pt conexiune outside de container


        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        props.put("schema.registry.url", "http://localhost:18081");
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
// @Configuration
//public class KafkaProducerConfig {
//
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
////        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
////        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.102:19092");
////        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "redpanda-0:9092"); pentru conduktor
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092");
//
//
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
////        props.put("schema.registry.url", "http://localhost:18081");
//        return props;
//    }
//
//    @Bean
//    public ProducerFactory<String, products> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs());
//    }
//
//    @Bean
//    public KafkaTemplate<String, products> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//}
