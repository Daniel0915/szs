package com.example.szs.module.redis;

import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.eNum.redis.ChannelType;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RedisPubModule {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisPublisher redisPublisher;
    // 각 Channel 별 Listener
    private final RedisSubscribeListener redisSubscribeListener;

    private ConcurrentHashMap<ChannelType, ChannelTopic> topicCache = new ConcurrentHashMap<>();

    /**
     * Channel 별 Message 전송
     * @param
     */
    public void pubMsgChannel(MessageDto message) {
        //1. 요청한 Channel 을 구독.
        ChannelTopic topic = topicCache.computeIfAbsent(message.getChannelType(), key -> {
            ChannelTopic newTopic = new ChannelTopic(key.name());
            redisMessageListenerContainer.addMessageListener(redisSubscribeListener, newTopic);
            return newTopic;
        });

        //2. Message 전송
        redisPublisher.publish(topic, message);
    }

    /**
     * Channel 구독 취소
     * @param channel
     */
    public void cancelSubChannel(String channel) {
        redisMessageListenerContainer.removeMessageListener(redisSubscribeListener);
    }
}
