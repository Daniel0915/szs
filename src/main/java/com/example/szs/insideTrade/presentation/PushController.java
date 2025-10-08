package com.example.szs.insideTrade.presentation;

import com.example.szs.insideTrade.infrastructure.push.SsePush;
import com.example.szs.insideTrade.infrastructure.push.dto.MessageDTO;
import com.example.szs.common.api.ResStatus;
import com.example.szs.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
@Slf4j
public class PushController {
    private final SsePush ssePush;
    private final ApiResponse apiResponse;

    @GetMapping(value = "/get-flux-message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MessageDTO>> getFluxMessages() {
        return ssePush.getFluxMessage()
                      .delayElements(Duration.ofMillis(5000));
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO message) {
        try {
            ssePush.sendMessage(message);
            return apiResponse.makeResponse(ResStatus.SUCCESS);
        } catch (Exception e) {
            log.error("예상하지 못한 예외 에러 발생 : ", e);
            return apiResponse.makeResponse(ResStatus.ERROR);
        }
    }
}
