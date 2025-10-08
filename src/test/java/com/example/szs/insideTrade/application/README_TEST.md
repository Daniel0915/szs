# insideTrade.application 서비스 테스트 가이드

이 문서는 `insideTrade.application` 패키지의 서비스 클래스들에 대한 테스트 코드를 설명합니다.

---

## 📚 목차
1. [테스트 코드란?](#테스트-코드란)
2. [테스트 기본 개념](#테스트-기본-개념)
3. [각 테스트 파일 설명](#각-테스트-파일-설명)
4. [테스트 실행 방법](#테스트-실행-방법)

---

## 🤔 테스트 코드란?

테스트 코드는 작성한 코드가 **제대로 동작하는지 자동으로 검증**하는 코드입니다.

### 왜 테스트 코드가 필요한가요?
- ✅ **버그 조기 발견**: 코드를 수정할 때마다 자동으로 문제를 찾아줌
- ✅ **리팩토링 안전성**: 코드를 개선할 때 기존 기능이 깨지지 않았는지 확인
- ✅ **문서화 역할**: 테스트 코드를 보면 해당 기능이 어떻게 동작하는지 이해 가능
- ✅ **협업 효율성**: 다른 개발자가 코드를 수정해도 테스트로 검증 가능

---

## 📖 테스트 기본 개념

### 1. 단위 테스트 (Unit Test)
- **하나의 메서드/기능**이 제대로 동작하는지 테스트
- 외부 의존성(DB, 네트워크 등)은 **Mock 객체**로 대체

### 2. Given-When-Then 패턴
테스트 코드를 **3단계로 구조화**하는 방법:

```java
@Test
void testExample() {
    // Given: 테스트를 위한 준비 (데이터 설정)
    String input = "테스트";
    
    // When: 실제 테스트할 메서드 실행
    String result = service.processInput(input);
    
    // Then: 결과 검증
    assertThat(result).isEqualTo("예상 결과");
}
```

### 3. Mock 객체
**가짜 객체**를 만들어서 실제 DB나 외부 API를 호출하지 않고 테스트합니다.

```java
// Mock 객체 생성
@Mock
private UserRepository userRepository;

// Mock 동작 설정
given(userRepository.findById(1L)).willReturn(mockUser);

// Mock 호출 검증
verify(userRepository).findById(1L);
```

### 4. 주요 어노테이션

| 어노테이션 | 설명 |
|-----------|------|
| `@Test` | 이 메서드가 테스트 메서드임을 표시 |
| `@ExtendWith(MockitoExtension.class)` | Mockito를 사용하기 위한 설정 |
| `@Mock` | 가짜 객체(Mock) 생성 |
| `@InjectMocks` | Mock 객체들을 주입받는 테스트 대상 |
| `@DisplayName` | 테스트 이름을 한글로 표시 |

### 5. 주요 검증 메서드 (Assertions)

```java
// 값이 같은지 확인
assertThat(result).isEqualTo(expected);

// null이 아닌지 확인
assertThat(result).isNotNull();

// 리스트 크기 확인
assertThat(list).hasSize(3);

// 리스트가 비어있는지 확인
assertThat(list).isEmpty();

// 메서드가 호출되었는지 확인
verify(repository).findAll();

// 메서드가 정확히 1번 호출되었는지 확인
verify(repository, times(1)).save(any());
```

---

## 📝 각 테스트 파일 설명

### 1. CorpInfoServiceTest.java
**테스트 대상**: `CorpInfoService` - 회사 정보 조회 서비스

#### 테스트하는 메서드
- `getAllCorpInfoDTOList()`: 모든 회사 정보를 DTO로 변환하여 조회

#### 테스트 케이스
1. **모든 회사 정보 조회 - 성공**
   ```java
   @Test
   void getAllCorpInfoDTOList_Success() {
       // Given: Mock 회사 데이터 2개 준비
       // When: 서비스 메서드 호출
       // Then: 2개의 DTO가 올바르게 반환되는지 검증
   }
   ```

2. **빈 리스트 조회**
   - DB에 데이터가 없을 때 빈 리스트가 반환되는지 확인

#### 핵심 개념
- **Builder 패턴**: `CorpInfo.builder()...build()`로 객체 생성
- **Mock 데이터**: 실제 DB 없이 가상의 데이터로 테스트
- **DTO 변환**: Entity를 DTO로 변환하는 로직 검증

---

### 2. UserServiceTest.java
**테스트 대상**: `UserService` - SSE(Server-Sent Events) 연결 관리 서비스

#### 테스트하는 메서드
- `connect(String clientId)`: 클라이언트 연결 생성

#### 테스트 케이스
1. **클라이언트 연결 - 성공**
   - 새로운 클라이언트가 연결되면 `SseEmitter` 객체가 생성되는지 확인

2. **여러 클라이언트 동시 연결**
   - 여러 클라이언트가 동시에 연결될 때 모두 관리되는지 확인

3. **같은 클라이언트 ID로 재연결**
   - 같은 ID로 재연결 시 이전 연결이 덮어씌워지는지 확인

#### 핵심 개념
- **ReflectionTestUtils**: private 필드에 접근하여 테스트
- **SSE**: 서버에서 클라이언트로 실시간 데이터 전송하는 기술
- **ConcurrentHashMap**: 멀티스레드 환경에서 안전한 Map

---

### 3. ScrapingServiceTest.java
**테스트 대상**: `ScrapingService` - 웹 크롤링 데이터 저장 서비스

#### 테스트하는 메서드
- `updateLargeHoldingsScrapingData()`: 대량보유 데이터 업데이트
- `updateExecOwnershipsScrapingData()`: 임원 소유 데이터 업데이트

#### 테스트 케이스
1. **대량보유 스크래핑 데이터 업데이트 - 성공**
   - 크롤링 API가 제대로 호출되는지 확인
   - 데이터가 DB에 저장되는지 확인

2. **임원 소유지분 스크래핑 데이터 업데이트 - 성공**
   - 임원 정보가 올바르게 전달되는지 확인

3. **여러 건의 데이터 업데이트**
   - 여러 개의 데이터를 한 번에 처리할 때 각각 처리되는지 확인

4. **빈 리스트로 업데이트**
   - 데이터가 없을 때 크롤링 API가 호출되지 않는지 확인

#### 핵심 개념
- **Mock 객체**: `mock(ExecOwnership.class)`로 복잡한 엔티티 모킹
- **given-willReturn**: Mock 객체의 동작 정의
- **verify**: 메서드가 예상대로 호출되었는지 검증

---

### 4. LargeHoldingsServiceTest.java
**테스트 대상**: `LargeHoldingsService` - 대량보유 관련 비즈니스 로직

#### 테스트하는 메서드
- `getSearchPageLargeHoldingsDetail()`: 페이징 처리된 상세 조회
- `getLargeHoldingsStockRatio()`: 주식 비율 조회 (null/0 필터링)
- `getLargeHoldingsMonthlyTradeCnt()`: 월별 거래 건수 조회
- `getLargeHoldingsTradeDtBy()`: 대량보유자별 거래일 조회

#### 테스트 케이스
1. **페이지 조회 - 성공**
   - Spring Data JPA의 페이징 기능 테스트
   - `Page` 객체를 `PageResDTO`로 변환하는 로직 검증

2. **주식 비율 조회 - 필터링**
   - `stkrt`가 `null`이거나 `0`인 데이터는 필터링되는지 확인
   - 스트림 API의 `filter()` 동작 검증

3. **월별 거래 건수 조회**
   - 매도(SELL)와 매수(BUY) 건수가 각각 조회되는지 확인

#### 핵심 개념
- **Pageable**: Spring Data JPA의 페이징 처리
- **Stream API**: `filter()`, `collect()` 등을 사용한 데이터 처리
- **Enum**: `SellOrBuyType.SELL`, `SellOrBuyType.BUY` 사용

---

### 5. ExecOwnershipServiceTest.java
**테스트 대상**: `ExecOwnershipService` - 임원 지분 소유 관련 비즈니스 로직

#### 테스트하는 메서드
- `getSearchPageExecOwnershipDetail()`: 페이징 처리된 상세 조회
- `getStockCntTop5()`: 주식 수량 Top5 조회
- `getMonthlyTradeCnt()`: 월별 거래 건수 조회
- `getTopStockTradeTotal()`: Top 거래 조회 (BUY/SELL/ALL)

#### 테스트 케이스
1. **페이지 조회 - 성공**
   - 페이징 기능 정상 동작 확인

2. **Top5 조회 - 5개 이상**
   - 10개 데이터 중 상위 5개만 반환되는지 확인
   - `stream().limit(5)` 동작 검증

3. **월별 거래 건수 조회**
   - 매도/매수 건수가 정확히 조회되는지 확인

4. **Top 거래 조회 - BUY/SELL/ALL**
   - `switch` 표현식을 사용한 조건부 로직 테스트
   - `SellOrBuyType.BUY`: 매수만 조회
   - `SellOrBuyType.SELL`: 매도만 조회
   - `SellOrBuyType.ALL`: 매수/매도 모두 조회

#### 핵심 개념
- **switch 표현식**: Java 14+의 새로운 switch 문법
- **Stream API**: `limit()`, `toList()` 등 사용
- **DTO 빌더 패턴**: DTO 객체를 Builder로 생성

---

## 🚀 테스트 실행 방법

### 1. 전체 테스트 실행
```bash
./gradlew test
```

### 2. 특정 패키지 테스트 실행
```bash
./gradlew test --tests "com.example.szs.insideTrade.application.*"
```

### 3. 특정 클래스 테스트 실행
```bash
./gradlew test --tests "com.example.szs.insideTrade.application.CorpInfoServiceTest"
```

### 4. 특정 메서드 테스트 실행
```bash
./gradlew test --tests "com.example.szs.insideTrade.application.CorpInfoServiceTest.getAllCorpInfoDTOList_Success"
```

### 5. IntelliJ에서 실행
- 테스트 파일을 열고 `Ctrl+Shift+F10` (Mac: `Cmd+Shift+R`)
- 또는 클래스/메서드 왼쪽의 녹색 화살표 클릭

---

## 📌 테스트 코드 작성 팁

### 1. 테스트 메서드 이름 규칙
```
테스트할메서드명_시나리오_예상결과()
```

예시:
- `getAllCorpInfoDTOList_Success()`: 성공 케이스
- `getAllCorpInfoDTOList_EmptyList()`: 빈 리스트 케이스
- `getLargeHoldingsStockRatio_WithNullValues()`: null 값 포함 케이스

### 2. Given-When-Then 구조 유지
```java
@Test
void exampleTest() {
    // Given: 준비
    // - Mock 데이터 설정
    // - 입력 값 준비
    
    // When: 실행
    // - 테스트할 메서드 호출
    
    // Then: 검증
    // - 결과 확인
    // - Mock 호출 검증
}
```

### 3. 하나의 테스트는 하나의 기능만
- 테스트 하나당 하나의 검증 목적만 가져야 함
- 여러 기능을 테스트하려면 여러 개의 테스트 메서드 작성

### 4. 테스트는 독립적이어야 함
- 테스트 간 실행 순서에 의존하면 안 됨
- 각 테스트는 독립적으로 실행 가능해야 함

---

## 🔍 자주 발생하는 문제와 해결방법

### 1. UnnecessaryStubbingException
**원인**: Mock 설정은 했지만 실제로 사용하지 않았을 때

**해결**:
```java
// ❌ 잘못된 예
ExecOwnershipDetail detail = mock(ExecOwnershipDetail.class);
given(detail.getCorpCode()).willReturn("00126380"); // 사용하지 않음

// ✅ 올바른 예
ExecOwnershipDetail detail = mock(ExecOwnershipDetail.class);
// getCorpCode()를 호출하는 경우에만 설정
```

### 2. NullPointerException
**원인**: Mock 객체를 설정하지 않았거나, 잘못 설정했을 때

**해결**:
```java
// Mock 객체 설정 확인
@Mock
private UserRepository userRepository;

@InjectMocks
private UserService userService; // 자동으로 Mock 주입됨
```

### 3. 빌더 패턴 오류
**원인**: 엔티티에 `@Builder`가 없어서 발생

**해결**:
```java
// 빌더가 없는 경우 Mock 사용
ExecOwnership execOwnership = mock(ExecOwnership.class);
given(execOwnership.getCorpCode()).willReturn("00126380");
```

---

## 📚 참고 자료

- [JUnit 5 공식 문서](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito 공식 문서](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ 공식 문서](https://assertj.github.io/doc/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)

---

## ✅ 다음 단계

1. **통합 테스트 작성**: 실제 DB를 사용하는 테스트
2. **테스트 커버리지 확인**: Jacoco 플러그인 사용
3. **CI/CD 연동**: GitHub Actions 등으로 자동 테스트 실행
4. **TDD 실천**: 테스트 먼저 작성 → 코드 작성 → 리팩토링

---

이 가이드를 참고하여 테스트 코드를 이해하고, 필요한 경우 추가 테스트를 작성해보세요! 🚀
