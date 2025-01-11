package com.example.szs.controller;

import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.module.redis.RedisPublisher;
import com.example.szs.module.redis.RedisSubscribeListener;
import com.example.szs.service.stock.ExecOwnershipService;
import com.example.szs.service.stock.LargeHoldingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis/pub-sub")
@CrossOrigin(origins = "http://127.0.0.1:5173")
@RequiredArgsConstructor
@Slf4j
public class RedisPubSubController {
    private final RedisPublisher redisPubService;
    private final RedisSubscribeListener redisSubscribeListener;
    private final ExecOwnershipService execOwnershipService;
    private final LargeHoldingsService largeHoldingsService;



    @PostMapping("/send")
    public void sendMessage(@RequestParam ChannelType channelType, @RequestBody MessageDto message) {
        log.info("Redis Pub MSG Channel = {}", channelType);
//        execOwnershipService.insertData();
//        largeHoldingsService.insertData();
//        redisPubService.pubMsgChannel(channelType, message);
    }

    @PostMapping("/cancle")
    public void cancelSubChannel(@RequestParam String channel) {
        redisPubService.cancelSubChannel(channel);
    }

    @GetMapping("/get-message")
    public DeferredResult<MessageDto> getDeferredMessages(@RequestParam Long userId) {
        return redisSubscribeListener.getDeferredMessages(userId);
    }

    @GetMapping(value = "/get-flux-message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MessageDto>> getFluxMessages(@RequestParam Long userId) {
        return redisSubscribeListener.getFluxMessage(userId).delayElements(Duration.ofMillis(5000));
    }
}
