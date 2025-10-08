package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.*;
import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.insideTrade.infrastructure.client.dto.ExecOwnershipDetailCrawlingDTO;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsDetailCrawlingDTO;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsStkrtCrawlingDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * ScrapingService 테스트
 *
 * 웹 크롤링으로 데이터를 가져와서 DB에 저장하는 로직을 테스트합니다.
 * - ArgumentCaptor: 메서드 호출 시 전달된 인자를 캡처하여 검증
 * - any(): 어떤 값이든 매칭되는 Mockito matcher
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ScrapingService 단위 테스트")
class ScrapingServiceTest {

    @Mock
    private Dart dart;

    @Mock
    private LargeHoldingsDetailRepo largeHoldingsDetailRepo;

    @Mock
    private LargeHoldingsStkrtRepo largeHoldingsStkrtRepo;

    @Mock
    private ExecOwnershipDetailRepo execOwnershipDetailRepo;

    @InjectMocks
    private ScrapingService scrapingService;

    @Test
    @DisplayName("대량보유 스크래핑 데이터 업데이트 - 성공")
    void updateLargeHoldingsScrapingData_Success() {
        // Given: 테스트용 LargeHoldings 데이터
        LargeHoldings largeHoldings = LargeHoldings.builder()
                .rceptNo("20240101000001")
                .corpCode("00126380")
                .corpName("삼성전자")
                .build();

        List<LargeHoldings> insertList = Arrays.asList(largeHoldings);

        // Mock 크롤링 데이터 - LargeHoldingsDetail
        LargeHoldingsDetailCrawlingDTO detailDTO = LargeHoldingsDetailCrawlingDTO.builder()
                .rceptNo("20240101000001")
                .corpCode("00126380")
                .corpName("삼성전자")
                .largeHoldingsName("홍길동")
                .birthDateOrBizRegNum("19800101")
                .tradeDt("20240101")
                .build();

        // Mock 크롤링 데이터 - LargeHoldingsStkrt
        LargeHoldingsStkrtCrawlingDTO stkrtDTO = LargeHoldingsStkrtCrawlingDTO.builder()
                .rceptNo("20240101000001")
                .corpCode("00126380")
                .corpName("삼성전자")
                .largeHoldingsName("홍길동")
                .stkrt(5.5F)
                .build();

        // Dart API 호출 결과 설정
        given(dart.getLargeHoldingsDetailCrawling(anyString(), anyString(), anyString()))
                .willReturn(Arrays.asList(detailDTO));
        given(dart.getLargeHoldingsStkrtCrawling(anyString(), anyString(), anyString()))
                .willReturn(Arrays.asList(stkrtDTO));

        // When: 스크래핑 데이터 업데이트 실행
        scrapingService.updateLargeHoldingsScrapingData(insertList);

        // Then: 검증
        // 1. Dart API가 정확히 1번씩 호출되었는지 확인
        verify(dart, times(1)).getLargeHoldingsDetailCrawling(
                eq("20240101000001"),
                eq("00126380"),
                eq("삼성전자")
        );
        verify(dart, times(1)).getLargeHoldingsStkrtCrawling(
                eq("20240101000001"),
                eq("00126380"),
                eq("삼성전자")
        );

        // 2. Repository의 저장 메서드가 호출되었는지 확인
        verify(largeHoldingsDetailRepo, times(1)).insertNativeBatch(anyList(), eq(500));
        verify(largeHoldingsStkrtRepo, times(1)).insertNativeBatch(anyList(), eq(500));
    }

    @Test
    @DisplayName("임원 소유지분 스크래핑 데이터 업데이트 - 성공")
    void updateExecOwnershipsScrapingData_Success() {
        // Given: 테스트용 ExecOwnership 데이터 (Mock 객체 사용)
        ExecOwnership execOwnership = mock(ExecOwnership.class);
        given(execOwnership.getRceptNo()).willReturn("20240101000002");
        given(execOwnership.getCorpCode()).willReturn("00164779");
        given(execOwnership.getCorpName()).willReturn("SK하이닉스");
        given(execOwnership.getRepror()).willReturn("김철수");
        given(execOwnership.getIsuExctvRgistAt()).willReturn("Y");
        given(execOwnership.getIsuExctvOfcps()).willReturn("대표이사");
        given(execOwnership.getIsuMainShrholdr()).willReturn("N");

        List<ExecOwnership> insertList = Arrays.asList(execOwnership);

        // Mock 크롤링 데이터
        ExecOwnershipDetailCrawlingDTO detailDTO = ExecOwnershipDetailCrawlingDTO.builder()
                .rceptNo("20240101000002")
                .corpCode("00164779")
                .corpName("SK하이닉스")
                .build();

        // Dart API 호출 결과 설정
        given(dart.getExecOwnershipDetailCrawling(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()
        )).willReturn(Arrays.asList(detailDTO));

        // When: 스크래핑 데이터 업데이트 실행
        scrapingService.updateExecOwnershipsScrapingData(insertList);

        // Then: 검증
        // 1. Dart API가 올바른 파라미터로 호출되었는지 확인
        verify(dart, times(1)).getExecOwnershipDetailCrawling(
                eq("20240101000002"),
                eq("00164779"),
                eq("SK하이닉스"),
                eq("김철수"),
                eq("Y"),
                eq("대표이사"),
                eq("N")
        );

        // 2. Repository의 저장 메서드가 호출되었는지 확인
        verify(execOwnershipDetailRepo, times(1)).insertNativeBatch(anyList(), eq(500));
    }

    @Test
    @DisplayName("여러 건의 대량보유 데이터 업데이트")
    void updateLargeHoldingsScrapingData_MultipleItems() {
        // Given: 여러 개의 LargeHoldings 데이터
        LargeHoldings holdings1 = LargeHoldings.builder()
                .rceptNo("20240101000001")
                .corpCode("00126380")
                .corpName("삼성전자")
                .build();

        LargeHoldings holdings2 = LargeHoldings.builder()
                .rceptNo("20240101000002")
                .corpCode("00164779")
                .corpName("SK하이닉스")
                .build();

        List<LargeHoldings> insertList = Arrays.asList(holdings1, holdings2);

        // Mock 크롤링 데이터 설정
        given(dart.getLargeHoldingsDetailCrawling(anyString(), anyString(), anyString()))
                .willReturn(Arrays.asList());
        given(dart.getLargeHoldingsStkrtCrawling(anyString(), anyString(), anyString()))
                .willReturn(Arrays.asList());

        // When: 스크래핑 데이터 업데이트 실행
        scrapingService.updateLargeHoldingsScrapingData(insertList);

        // Then: 각 항목에 대해 Dart API가 호출되었는지 확인
        // 2개의 데이터 * 2개의 API 호출 = 총 4번
        verify(dart, times(2)).getLargeHoldingsDetailCrawling(anyString(), anyString(), anyString());
        verify(dart, times(2)).getLargeHoldingsStkrtCrawling(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("빈 리스트로 업데이트 시도")
    void updateLargeHoldingsScrapingData_EmptyList() {
        // Given: 빈 리스트
        List<LargeHoldings> emptyList = Arrays.asList();

        // When: 빈 리스트로 업데이트
        scrapingService.updateLargeHoldingsScrapingData(emptyList);

        // Then: Dart API가 호출되지 않아야 함
        verify(dart, times(0)).getLargeHoldingsDetailCrawling(anyString(), anyString(), anyString());
        verify(dart, times(0)).getLargeHoldingsStkrtCrawling(anyString(), anyString(), anyString());
    }
}
