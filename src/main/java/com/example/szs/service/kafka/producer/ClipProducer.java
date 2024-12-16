package com.example.szs.service.kafka.producer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service

public class ClipProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RoutingKafkaTemplate routingKafkaTemplate;
    private final ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    public ClipProducer(KafkaTemplate<String, String> kafkaTemplate,
                        RoutingKafkaTemplate routingKafkaTemplate,
                        ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.routingKafkaTemplate = routingKafkaTemplate;
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    public void async(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex) -> {
            // onSuccess
            if (ex == null) {
                System.out.println("성공 send 메시지 - async");
            // onFailure
            } else {
                ex.printStackTrace();
            }
        });
    }

    public void sync(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        try {
            System.out.println("성공 send 메시지 - sync");
            future.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public void routingSend(String topic, String message) {
        routingKafkaTemplate.send(topic, message);
    }

    public void routingSendByte(String topic, byte[] message) {
        routingKafkaTemplate.send(topic, message);
    }

    public void replyingSend(String topic, String message) throws ExecutionException, InterruptedException, TimeoutException {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        RequestReplyFuture<String, String, String> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
        ConsumerRecord<String, String> consumerRecord = replyFuture.get(100, TimeUnit.SECONDS);
        System.out.println(consumerRecord.value());
    }
}
