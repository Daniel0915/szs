//package com.example.szs.config.kafka;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import org.springframework.kafka.listener.GenericMessageListenerContainer;
//import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
//
//@Configuration
//public class ReplayingKafkaTemplateConfiguration {
//
//    @Bean
//    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(ProducerFactory<String, String> producerFactory,
//                                                                               ConcurrentMessageListenerContainer<String, String> repliesContainer) {
//        return new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
//
//    }
//
//    @Bean
//    public ConcurrentMessageListenerContainer<String, String> repliesContainer(ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
//        ConcurrentMessageListenerContainer<String, String> container = containerFactory.createContainer("clips-replies");
//        container.getContainerProperties().setGroupId("clips-replies-container-id");
//        return container;
//    }
//}
