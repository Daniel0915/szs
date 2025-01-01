package com.example.szs.controller;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.module.redis.RedisPublisher;
import com.example.szs.service.redis.RedisPubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/redis/pub-sub")
@RequiredArgsConstructor
@Slf4j
public class RedisPubSubController {
    private final RedisPubService redisPubService;

    @PostMapping("/send")
    public void sendMessage(@RequestParam String channel, @RequestBody MessageDto message) {
        log.info("Redis Pub MSG Channel = {}", channel);
        redisPubService.pubMsgChannel(channel, message);
    }

    @PostMapping("/cancle")
    public void cancelSubChannel(@RequestParam String channel) {
        redisPubService.cancelSubChannel(channel);
    }
}
