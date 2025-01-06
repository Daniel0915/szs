package com.example.szs.module.redis;

import com.example.szs.domain.subscribe.UserPushSubs;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.queryDSLSearch.UserPushSubsSearchCondition;
import com.example.szs.repository.subscribe.UserPushSubsRepositoryCustom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private final UserPushSubsRepositoryCustom userPushSubsRepositoryCustom;
    private final ConcurrentHashMap<Long, LinkedBlockingQueue<MessageDto>> userMessageQueues = new ConcurrentHashMap<>();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = template.getStringSerializer()
                                            .deserialize(message.getBody());

            MessageDto messageDto = objectMapper.readValue(publishMessage, MessageDto.class);

            // 1. 특정 채널에 구독한 회원 DB 조회
            List<UserPushSubs> userPushSubsList = userPushSubsRepositoryCustom.getUserPushSubsListBy(UserPushSubsSearchCondition.builder()
                                                                                                                                .channelType(messageDto.getChannelType())
                                                                                                                                .build());
            // 2. add message
            for (UserPushSubs userPushSubs : userPushSubsList) {
                ChannelType publishChannelType = messageDto.getChannelType();
                ChannelType userSubChannelType = ChannelType.findChannelTypeOrNull(userPushSubs.getChannelType());

                if (publishChannelType == userSubChannelType) {
                    Long userId = userPushSubs.getUserInfo().getUserId();
                    addMessage(userMessageQueues, userId, messageDto);
                }
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void addMessage(ConcurrentHashMap<Long, LinkedBlockingQueue<MessageDto>> map, Long userId, MessageDto messageDto) {
        // 큐가 없으면 초기화하여 삽입
        map.computeIfAbsent(userId, key -> new LinkedBlockingQueue<>()).add(messageDto);
    }

    public List<MessageDto> drainAllMessages(Long userId) {
        LinkedBlockingQueue<MessageDto> queue = userMessageQueues.get(userId);
        List<MessageDto> messageDtoList = new ArrayList<>();
        if (queue != null) {
            queue.drainTo(messageDtoList);  // 큐의 모든 요소를 messageList로 이동
        }
        return messageDtoList;
    }
}
