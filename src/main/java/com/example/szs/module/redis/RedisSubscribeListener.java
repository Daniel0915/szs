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
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
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
    private final ConcurrentHashMap<Long, DeferredResult<MessageDto>> userDeferredResults = new ConcurrentHashMap<>();


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
                    addMessage(userId, messageDto);
                }
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void addMessage(Long userId, MessageDto messageDto) {
        // 큐가 없으면 초기화하여 삽입
        userMessageQueues.computeIfAbsent(userId, key -> new LinkedBlockingQueue<>()).add(messageDto);

        // DeferredResult가 있으면 큐에서 메시지를 전달
        DeferredResult<MessageDto> deferredResult = userDeferredResults.get(userId);

        if (deferredResult != null) {
            MessageDto message = userMessageQueues.get(userId).poll(); // 큐에서 첫 번째 메시지 가져오기
            if (message != null) { // 메시지가 있으면 응답
                deferredResult.setResult(message);
                userDeferredResults.remove(userId); // 응답 후 deferredResult 삭제
            }
        }
    }

    public DeferredResult<MessageDto> getDeferredMessages(Long userId) {
        final long timeoutInMillis = 30000; // 30초

        // 타임아웃 설정된 DeferredResult 객체 생성
        DeferredResult<MessageDto> deferredResult = new DeferredResult<>(timeoutInMillis);

        // 타임아웃 시 처리할 콜백 설정
        deferredResult.onTimeout(() -> {
            log.warn("Timeout occurred for userId: {}", userId);
            deferredResult.setResult(null); // 타임아웃 시 null을 응답
        });

        // DeferredResult 저장
        userDeferredResults.put(userId, deferredResult);

        // 메시지가 준비되었을 때 처리하기 위한 큐
        LinkedBlockingQueue<MessageDto> queue = userMessageQueues.get(userId);
        if (queue != null && !queue.isEmpty()) {
            MessageDto message = queue.poll(); // 이미 대기 중인 메시지가 있으면 즉시 처리
            if (message != null) {
                deferredResult.setResult(message);
                userDeferredResults.remove(userId); // 응답 후 DeferredResult 삭제
            }
        }

        return deferredResult;
    }

    public Flux<ServerSentEvent<MessageDto>> getFluxMessage(Long userId) {
        return Flux.create(sink -> {
            // 메시지 큐에서 데이터를 Flux 로 스트림 전송
            LinkedBlockingQueue<MessageDto> queue = userMessageQueues.get(userId);

            // 메세지가 없으면 스트림 종료
            if (CollectionUtils.isEmpty(queue)) {
                sink.complete();
                return;
            }

            // 메세지를 Flux 스트림에 추가
            while (!queue.isEmpty()) {
                MessageDto messageDto = queue.poll();
                ServerSentEvent<MessageDto> event = ServerSentEvent.builder(messageDto)
                                                                   .build();

                sink.next(event);
            }
        });
    }
}
