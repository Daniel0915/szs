package com.example.szs.insideTrade.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserService 테스트
 *
 * SSE(Server-Sent Events) 연결 관리를 테스트합니다.
 * - ReflectionTestUtils: private 필드에 접근하기 위한 유틸리티
 * - SseEmitter: 서버에서 클라이언트로 이벤트를 보내는 객체
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    private Map<String, SseEmitter> clients;

    @BeforeEach
    void setUp() {
        // UserService의 private 필드인 clients에 접근하기 위해 ReflectionTestUtils 사용
        // 테스트를 위해 새로운 Map을 주입합니다
        clients = new ConcurrentHashMap<>();
        ReflectionTestUtils.setField(userService, "clients", clients);
    }

    @Test
    @DisplayName("클라이언트 연결 - 성공")
    void connect_Success() {
        // Given: 클라이언트 ID 준비
        String clientId = "test-client-123";

        // When: 클라이언트 연결
        SseEmitter emitter = userService.connect(clientId);

        // Then: 검증
        // 1. SseEmitter가 생성되었는지 확인
        assertThat(emitter).isNotNull();

        // 2. clients Map에 클라이언트가 추가되었는지 확인
        assertThat(clients).containsKey(clientId);
        assertThat(clients.get(clientId)).isEqualTo(emitter);

        // 3. clients의 크기가 1인지 확인
        assertThat(clients).hasSize(1);
    }

    @Test
    @DisplayName("여러 클라이언트 동시 연결")
    void connect_MultipleClients() {
        // Given: 여러 클라이언트 ID
        String client1 = "client-1";
        String client2 = "client-2";
        String client3 = "client-3";

        // When: 여러 클라이언트 연결
        SseEmitter emitter1 = userService.connect(client1);
        SseEmitter emitter2 = userService.connect(client2);
        SseEmitter emitter3 = userService.connect(client3);

        // Then: 모든 클라이언트가 등록되었는지 확인
        assertThat(clients).hasSize(3);
        assertThat(clients).containsKeys(client1, client2, client3);
        assertThat(emitter1).isNotNull();
        assertThat(emitter2).isNotNull();
        assertThat(emitter3).isNotNull();
    }

    @Test
    @DisplayName("같은 클라이언트 ID로 재연결")
    void connect_SameClientIdTwice() {
        // Given: 같은 클라이언트 ID
        String clientId = "duplicate-client";

        // When: 같은 ID로 두 번 연결
        SseEmitter firstEmitter = userService.connect(clientId);
        SseEmitter secondEmitter = userService.connect(clientId);

        // Then: 두 번째 연결이 첫 번째를 덮어씁니다
        assertThat(clients).hasSize(1);
        assertThat(clients.get(clientId)).isEqualTo(secondEmitter);
        assertThat(firstEmitter).isNotEqualTo(secondEmitter);
    }

    @Test
    @DisplayName("빈 상태에서 연결 확인")
    void connect_FromEmptyState() {
        // Given: 클라이언트가 없는 상태 (setUp에서 이미 빈 Map으로 초기화됨)
        assertThat(clients).isEmpty();

        // When: 첫 번째 클라이언트 연결
        String clientId = "first-client";
        SseEmitter emitter = userService.connect(clientId);

        // Then: 정상적으로 연결됨
        assertThat(clients).hasSize(1);
        assertThat(emitter).isNotNull();
    }
}
