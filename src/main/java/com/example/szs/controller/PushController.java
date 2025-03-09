package com.example.szs.controller;

import com.example.szs.model.dto.MessageDto;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.module.ApiResponse;
import com.example.szs.module.redis.RedisPublisher;
import com.example.szs.module.redis.RedisSubscribeListener;
import com.example.szs.service.stock.PushService;
import com.example.szs.utils.Response.ResUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
@Slf4j
public class PushController {
    private final PushService pushService;
    private final ApiResponse apiResponse;

    @GetMapping(value = "/get-flux-message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MessageDto>> getFluxMessages() {
        return pushService.getFluxMessage().delayElements(Duration.ofMillis(5000));
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto message) {
        try {
            pushService.sendMessage(message);
            return apiResponse.makeResponse(ResStatus.SUCCESS);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }
}
