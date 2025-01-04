package com.example.szs.controller;
import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.module.redis.RedisSubscribeListener;
import com.example.szs.module.redis.RedisPubModule;
import com.example.szs.service.stock.ExecOwnershipService;
import com.example.szs.service.stock.LargeHoldingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/redis/pub-sub")
@RequiredArgsConstructor
@Slf4j
public class RedisPubSubController {
    private final RedisPubModule redisPubService;
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
    public List<String> getMessages(@RequestParam String userId) {
        return redisSubscribeListener.drainAllMessages(userId);
    }
}
