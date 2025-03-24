package com.example.szs.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    // UUID 기반 클라이언트 관리
    private final        Map<String, SseEmitter> clients = new ConcurrentHashMap<>();
    private static final long                    TIME    = 60_000L;

    public SseEmitter connect(@PathVariable String clientId) {
        SseEmitter emitter = new SseEmitter(TIME);
        clients.put(clientId, emitter);

        sendActiveUserCount(); // 신규 접속 시 모든 사용자에게 업데이트

        emitter.onCompletion(() -> disconnect(clientId));
        emitter.onTimeout(() -> disconnect(clientId));

        return emitter;
    }

    private void disconnect(String clientId) {
        clients.remove(clientId);
        sendActiveUserCount(); // 연결 해제 시 사용자 수 업데이트
    }

    private void sendActiveUserCount() {
        int activeUsers = clients.size();
        clients.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event().data(activeUsers));
            } catch (IOException e) {
                emitter.complete();
                clients.remove(id);
            }
        });
    }
}
