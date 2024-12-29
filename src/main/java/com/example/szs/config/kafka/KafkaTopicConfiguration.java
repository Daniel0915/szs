//package com.example.szs.config.kafka;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//import org.springframework.kafka.core.KafkaAdmin;
//
//@Configuration
//public class KafkaTopicConfiguration {
//
//    // 여러개의 토픽을 받을 수 있음(NewTopic 의 기능)
//    @Bean
//    public KafkaAdmin.NewTopics clip2s() {
//        return new KafkaAdmin.NewTopics(
//                TopicBuilder.name("clip3").build(),
//                TopicBuilder.name("clip3-bytes").build(),
//                TopicBuilder.name("clip3-request").build(),
//                TopicBuilder.name("clip3-replies").build()
//        );
//    }
//}
