//package com.example.szs.config.kafka;
//
//
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//// TODO : 카프카 설정 후 주석 풀기
////@Configuration
//public class KafkaTemplateConfiguration {
//
//
////    @Bean
////    public KafkaTemplate<String, String> kafkaTemplate() {
////        return new KafkaTemplate<>(producerFactory());
////    }
////
////    private ProducerFactory<String, String> producerFactory() {
////        return new DefaultKafkaProducerFactory<>(producerProps());
////    }
//
////    private Map<String, Object> producerProps() {
////        Map<String, Object> props = new HashMap<>();
////        /*
////         * [설정]
////         * 1. 서버 클러스터
////         * 2. key 직렬화
////         * 3. value 직렬화
////         */
////        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
////        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
////        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
////        return props;
////    }
//}
