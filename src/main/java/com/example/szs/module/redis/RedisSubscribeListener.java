package com.example.szs.module.redis;

import com.example.szs.model.dto.MessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscribeListener implements MessageListener {
    // 유저별
    private final RedisTemplate<String, Object> template;
    private final ObjectMapper objectMapper;

    private ConcurrentHashMap<String, LinkedBlockingQueue<String>> userMessageQueues = new ConcurrentHashMap<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = template.getStringSerializer()
                                            .deserialize(message.getBody());

            MessageDto messageDto = objectMapper.readValue(publishMessage, MessageDto.class);

            String userId = "to9251";

            // 1. 특정 채널에 구독한 회원 DB 조회
            // 2. add message
            addMessage(userMessageQueues, userId, messageDto.getMessage());

            // Return || Another Method Call(etc.save to DB)
            // TODO
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void addMessage(ConcurrentHashMap<String, LinkedBlockingQueue<String>> map, String user, String message) {
        // 큐가 없으면 초기화하여 삽입
        map.computeIfAbsent(user, key -> new LinkedBlockingQueue<>()).add(message);
    }

    public List<String> drainAllMessages(String user) {
        LinkedBlockingQueue<String> queue = userMessageQueues.get(user);
        List<String> messageList = new java.util.ArrayList<>();
        if (queue != null) {
            queue.drainTo(messageList);  // 큐의 모든 요소를 messageList로 이동
        }
        return messageList;
    }
}
