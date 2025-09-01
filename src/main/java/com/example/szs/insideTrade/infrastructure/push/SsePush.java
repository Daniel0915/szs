package com.example.szs.insideTrade.infrastructure.push;

import com.example.szs.insideTrade.infrastructure.push.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
@Slf4j
public class SsePush {
    // 연결된 모든 사용자에게 메시지를 전달하는 Sink
    private final Sinks.Many<MessageDTO> messageSink = Sinks.many().multicast().onBackpressureBuffer();

    /**
     * 비회원 사용자가 구독할 Flux 생성
     * 새로운 Flux 를 각 구독자마다 생성하여, 계속해서 메시지를 받을 수 있도록 함
     */
    public Flux<ServerSentEvent<MessageDTO>> getFluxMessage() {
        return messageSink.asFlux()
                          .map(message -> ServerSentEvent.builder(message).build())
                          .doOnCancel(() -> log.info("연결 해제됨"));
    }

    /**
     * 메시지 전송
     */
    public void sendMessage(MessageDTO message) {
        messageSink.tryEmitNext(message);
    }
}
