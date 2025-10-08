package com.example.szs.insideTrade.presentation;

import com.example.szs.insideTrade.infrastructure.push.dto.MessageDTO;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.insideTrade.infrastructure.redis.RedisPublisher;
import com.example.szs.insideTrade.infrastructure.redis.RedisSubscribeListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/redis/pub-sub")
@RequiredArgsConstructor
@Slf4j
public class RedisPubSubController {
    private final RedisPublisher redisPubService;
    private final RedisSubscribeListener redisSubscribeListener;

    @PostMapping("/send")
    public void sendMessage(@RequestParam ChannelType channelType, @RequestBody MessageDTO message) {
        log.info("Redis Pub MSG Channel = {}", channelType);
    }

    @PostMapping("/cancel")
    public void cancelSubChannel(@RequestParam String channel) {
        redisPubService.cancelSubChannel(channel);
    }

    @GetMapping("/get-message")
    public DeferredResult<MessageDTO> getDeferredMessages(@RequestParam Long userId) {
        return redisSubscribeListener.getDeferredMessages(userId);
    }

    @GetMapping(value = "/ge-flux-message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MessageDTO>> getUserFluxMessages(@RequestParam Long userId) {
        return redisSubscribeListener.getFluxMessage(userId).delayElements(Duration.ofMillis(5000));
    }
}
