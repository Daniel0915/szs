package com.example.szs.insideTrade.application;

import com.example.szs.common.eNum.stock.SellOrBuyType;
import com.example.szs.insideTrade.application.dto.LargeHoldingsDetailDTO;
import com.example.szs.insideTrade.domain.*;
import com.example.szs.insideTrade.infrastructure.push.SsePush;
import com.example.szs.insideTrade.presentation.dto.request.LargeHoldingStkrtSearchConditionReqDTO;
import com.example.szs.insideTrade.presentation.dto.request.LargeHoldingsDetailSearchConditionReqDTO;
import com.example.szs.insideTrade.presentation.dto.response.LargeHoldingsStkrtResDTO;
import com.example.szs.insideTrade.presentation.dto.response.PageResDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * LargeHoldingsService 테스트
 *
 * 대량보유 관련 비즈니스 로직을 테스트합니다.
 * - Mock 객체를 사용하여 의존성을 모킹
 * - 페이징 처리 테스트
 * - 주식 비율 조회 및 필터링 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LargeHoldingsService 단위 테스트")
class LargeHoldingsServiceTest {

    @Mock
    private LargeHoldingsDomainService largeHoldingsDomainService;

    @Mock
    private ScrapingService scrapingService;

    @Mock
    private SsePush ssePush;

    @Mock
    private CorpInfoRepo corpInfoRepo;

    @Mock
    private LargeHoldingsDetailRepo largeHoldingsDetailRepo;

    @Mock
    private LargeHoldingsStkrtRepo largeHoldingsStkrtRepo;

    @InjectMocks
    private LargeHoldingsService largeHoldingsService;

    @Test
    @DisplayName("대량보유 상세 페이지 조회 - 성공")
    void getSearchPageLargeHoldingsDetail_Success() {
        // Given: 페이징 요청 데이터 및 Mock 엔티티
        LargeHoldingsDetailSearchConditionReqDTO condition = LargeHoldingsDetailSearchConditionReqDTO.builder()
                .corpCodeEq("00126380")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // Mock 객체 생성
        LargeHoldingsDetail detail1 = mock(LargeHoldingsDetail.class);
        LargeHoldingsDetail detail2 = mock(LargeHoldingsDetail.class);

        List<LargeHoldingsDetail> content = Arrays.asList(detail1, detail2);
        Page<LargeHoldingsDetail> page = new PageImpl<>(content, pageable, 2);

        given(largeHoldingsDetailRepo.searchPage(condition, pageable)).willReturn(page);

        // When: 페이지 조회
        PageResDTO result = largeHoldingsService.getSearchPageLargeHoldingsDetail(condition, pageable);

        // Then: 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(largeHoldingsDetailRepo).searchPage(condition, pageable);
    }

    @Test
    @DisplayName("주식 비율 조회 - null과 0 필터링")
    void getLargeHoldingsStockRatio_Success() {
        // Given: Mock 데이터 - 비율이 있는 데이터와 없는 데이터 혼합
        LargeHoldingsStkrt stkrt1 = mock(LargeHoldingsStkrt.class);
        given(stkrt1.getSeq()).willReturn(1L);
        given(stkrt1.getStkrt()).willReturn(5.5F);
        given(stkrt1.getLargeHoldingsName()).willReturn("홍길동");

        LargeHoldingsStkrt stkrt2 = mock(LargeHoldingsStkrt.class);
        given(stkrt2.getStkrt()).willReturn(null); // 필터링 대상

        LargeHoldingsStkrt stkrt3 = mock(LargeHoldingsStkrt.class);
        given(stkrt3.getStkrt()).willReturn(0.0F); // 필터링 대상

        List<LargeHoldingsStkrt> mockList = Arrays.asList(stkrt1, stkrt2, stkrt3);

        given(largeHoldingsStkrtRepo.getLargeHoldingsStockRatio(any())).willReturn(mockList);

        // When: 주식 비율 조회
        LargeHoldingStkrtSearchConditionReqDTO condition = LargeHoldingStkrtSearchConditionReqDTO.builder()
                .corpCode("00126380")
                .limit(1L)
                .build();
        List<LargeHoldingsStkrtResDTO> result = largeHoldingsService.getLargeHoldingsStockRatio(condition);

        // Then: null이거나 0인 비율은 필터링되어 1개만 반환
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("월별 거래 건수 조회 - 성공")
    void getLargeHoldingsMonthlyTradeCnt_Success() {
        // Given: 회사 코드
        String corpCode = "00126380";

        // Mock 매도/매수 데이터
        List<LargeHoldingsDetailDTO.MonthlyCountDTO> sellCountList = Arrays.asList(
                LargeHoldingsDetailDTO.MonthlyCountDTO.builder().month("202401").count(5L).build(),
                LargeHoldingsDetailDTO.MonthlyCountDTO.builder().month("202402").count(3L).build()
        );

        List<LargeHoldingsDetailDTO.MonthlyCountDTO> buyCountList = Arrays.asList(
                LargeHoldingsDetailDTO.MonthlyCountDTO.builder().month("202401").count(7L).build(),
                LargeHoldingsDetailDTO.MonthlyCountDTO.builder().month("202402").count(4L).build()
        );

        given(largeHoldingsDetailRepo.getLargeHoldingsMonthlyTradeCnt(corpCode, true))
                .willReturn(sellCountList);
        given(largeHoldingsDetailRepo.getLargeHoldingsMonthlyTradeCnt(corpCode, false))
                .willReturn(buyCountList);

        // When: 월별 거래 건수 조회
        List<LargeHoldingsDetailDTO.SellOrBuyMonthlyCountResponse> result =
                largeHoldingsService.getLargeHoldingsMonthlyTradeCnt(corpCode);

        // Then: 매도/매수 2개 반환
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSellOrBuyType()).isEqualTo(SellOrBuyType.SELL.getCode());
        assertThat(result.get(1).getSellOrBuyType()).isEqualTo(SellOrBuyType.BUY.getCode());

        verify(largeHoldingsDetailRepo).getLargeHoldingsMonthlyTradeCnt(corpCode, true);
        verify(largeHoldingsDetailRepo).getLargeHoldingsMonthlyTradeCnt(corpCode, false);
    }

    @Test
    @DisplayName("대량보유자별 거래일 조회 - 성공")
    void getLargeHoldingsTradeDtBy_Success() {
        // Given
        String corpCode = "00126380";
        String largeHoldingsName = "홍길동";

        LargeHoldingsDetail detail = mock(LargeHoldingsDetail.class);

        given(largeHoldingsDetailRepo.getLargeHoldingsDetailListBy(any()))
                .willReturn(Arrays.asList(detail));

        // When
        List<LargeHoldingsDetail> result =
                largeHoldingsService.getLargeHoldingsTradeDtBy(corpCode, largeHoldingsName);

        // Then
        assertThat(result).hasSize(1);
        verify(largeHoldingsDetailRepo).getLargeHoldingsDetailListBy(any());
    }
}
