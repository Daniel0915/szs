package com.example.szs.insideTrade.infrastructure.redis;

import com.example.szs.insideTrade.infrastructure.push.dto.MessageDTO;
import com.example.szs.model.eNum.redis.ChannelType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RedisPublisher {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    // 각 Channel 별 Listener
    private final RedisSubscribeListener redisSubscribeListener;
    private final RedisTemplate<String, Object> template;

    private final ConcurrentHashMap<ChannelType, ChannelTopic> topicCache = new ConcurrentHashMap<>();

    /**
     * Channel 별 Message 전송
     * @param message
     */
    public void pubMsgChannel(MessageDTO message) {
        //1. 요청한 Channel 을 구독.
        ChannelTopic topic = topicCache.computeIfAbsent(message.getChannelType(), key -> {
            ChannelTopic newTopic = new ChannelTopic(key.name());
            redisMessageListenerContainer.addMessageListener(redisSubscribeListener, newTopic);
            return newTopic;
        });

        //2. Message 전송
        this.publish(topic, message);
    }

    /**
     * Channel 구독 취소
     * @param channel
     */
    public void cancelSubChannel(String channel) {
        // TODO : 차후 회원 조회해서 채널 삭제
        redisMessageListenerContainer.removeMessageListener(redisSubscribeListener);
    }

    /**
     * Object publish
     */
    private void publish(ChannelTopic topic, MessageDTO dto) {
        template.convertAndSend(topic.getTopic(), dto);
    }

    /**
     * String publish
     */
    private void publish(ChannelTopic topic , String data) {
        template.convertAndSend(topic.getTopic(), data);
    }
}
