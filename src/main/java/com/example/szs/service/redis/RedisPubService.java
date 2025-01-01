package com.example.szs.service.redis;

import com.example.szs.model.dto.MessageDto;
import com.example.szs.module.redis.RedisPublisher;
import com.example.szs.module.redis.RedisSubscribeListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RedisPubService {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisPublisher redisPublisher;

    // 각 Channel 별 Listener
    private final RedisSubscribeListener redisSubscribeListener;

    /**
     * Channel 별 Message 전송
     * @param
     */
    public void pubMsgChannel(String channel, MessageDto message) {
        //1. 요청한 Channel 을 구독.
        redisMessageListenerContainer.addMessageListener(redisSubscribeListener, new ChannelTopic(channel));

        //2. Message 전송
        redisPublisher.publish(new ChannelTopic(channel), message);
    }

    /**
     * Channel 구독 취소
     * @param channel
     */
    public void cancelSubChannel(String channel) {
        redisMessageListenerContainer.removeMessageListener(redisSubscribeListener);
    }
}
