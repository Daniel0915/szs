package com.example.szs.insideTrade.application;

import com.example.szs.common.api.ApiResponse;
import com.example.szs.common.eNum.stock.SellOrBuyType;
import com.example.szs.insideTrade.application.dto.ExecOwnershipDetailDTO;
import com.example.szs.insideTrade.domain.*;
import com.example.szs.insideTrade.infrastructure.push.SsePush;
import com.example.szs.insideTrade.presentation.dto.request.ExecOwnershipDetailSearchConditionReqDTO;
import com.example.szs.insideTrade.presentation.dto.response.ExecOwnershipResDTO;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * ExecOwnershipService 테스트
 *
 * 임원 지분 소유 관련 비즈니스 로직을 테스트합니다.
 * - Mock 객체를 사용하여 복잡한 엔티티 모킹
 * - switch 표현식 테스트 (BUY/SELL/ALL)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExecOwnershipService 단위 테스트")
class ExecOwnershipServiceTest {

    @Mock
    private CorpInfoRepo corpInfoRepo;

    @Mock
    private ExecOwnershipDomainService execOwnershipDomainService;

    @Mock
    private ScrapingService scrapingService;

    @Mock
    private ExecOwnershipDetailRepo execOwnershipDetailRepo;

    @Mock
    private ExecOwnershipRepo execOwnershipRepo;

    @Mock
    private SsePush ssePush;

    @Mock
    private ApiResponse apiResponse;

    @InjectMocks
    private ExecOwnershipService execOwnershipService;

    @Test
    @DisplayName("임원 소유 상세 페이지 조회 - 성공")
    void getSearchPageExecOwnershipDetail_Success() {
        // Given: Mock 페이지 데이터
        ExecOwnershipDetailSearchConditionReqDTO condition = ExecOwnershipDetailSearchConditionReqDTO.builder()
                .corpCodeEq("00126380")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        ExecOwnershipDetail detail1 = mock(ExecOwnershipDetail.class);
        ExecOwnershipDetail detail2 = mock(ExecOwnershipDetail.class);

        List<ExecOwnershipDetail> content = Arrays.asList(detail1, detail2);
        Page<ExecOwnershipDetail> page = new PageImpl<>(content, pageable, 2);

        given(execOwnershipDetailRepo.searchPage(condition, pageable)).willReturn(page);

        // When: 페이지 조회
        PageResDTO result = execOwnershipService.getSearchPageExecOwnershipDetail(condition, pageable);

        // Then: 검증
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(execOwnershipDetailRepo).searchPage(condition, pageable);
    }

    @Test
    @DisplayName("주식 수량 Top5 조회 - 5개 이상")
    void getStockCntTop5_MoreThan5() {
        // Given: 10개의 임원 소유 데이터 (Mock)
        ExecOwnership[] mockArray = new ExecOwnership[10];
        for (int i = 0; i < 10; i++) {
            mockArray[i] = mock(ExecOwnership.class);
        }
        List<ExecOwnership> mockList = Arrays.asList(mockArray);

        given(execOwnershipRepo.getExecOwnershipOrderSpStockLmpCnt(anyString())).willReturn(mockList);

        // When: Top5 조회
        List<ExecOwnershipResDTO> result = execOwnershipService.getStockCntTop5("00126380");

        // Then: 상위 5개만 반환
        assertThat(result).hasSize(5);
    }

    @Test
    @DisplayName("월별 거래 건수 조회 - 성공")
    void getMonthlyTradeCnt_Success() {
        // Given: 회사 코드
        String corpCode = "00126380";

        // Mock 매도/매수 데이터
        List<ExecOwnershipDetailDTO.MonthlyCountDTO> sellCountList = Arrays.asList(
                ExecOwnershipDetailDTO.MonthlyCountDTO.builder().month("202401").count(5L).build(),
                ExecOwnershipDetailDTO.MonthlyCountDTO.builder().month("202402").count(3L).build()
        );

        List<ExecOwnershipDetailDTO.MonthlyCountDTO> buyCountList = Arrays.asList(
                ExecOwnershipDetailDTO.MonthlyCountDTO.builder().month("202401").count(7L).build(),
                ExecOwnershipDetailDTO.MonthlyCountDTO.builder().month("202402").count(4L).build()
        );

        given(execOwnershipDetailRepo.getMonthlyTradeCnt(corpCode, true)).willReturn(sellCountList);
        given(execOwnershipDetailRepo.getMonthlyTradeCnt(corpCode, false)).willReturn(buyCountList);

        // When: 월별 거래 건수 조회
        List<ExecOwnershipDetailDTO.SellOrBuyMonthlyCountResponse> result =
                execOwnershipService.getMonthlyTradeCnt(corpCode);

        // Then: 매도/매수 2개 반환
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSellOrBuyType()).isEqualTo(SellOrBuyType.SELL.getCode());
        assertThat(result.get(1).getSellOrBuyType()).isEqualTo(SellOrBuyType.BUY.getCode());

        verify(execOwnershipDetailRepo).getMonthlyTradeCnt(corpCode, true);
        verify(execOwnershipDetailRepo).getMonthlyTradeCnt(corpCode, false);
    }

    @Test
    @DisplayName("Top 거래 조회 - BUY만")
    void getTopStockTradeTotal_BuyOnly() {
        // Given: 매수 데이터만 조회
        String tradeDtGoe = "20240101";
        String tradeDtLoe = "20240131";
        SellOrBuyType sellOrBuyType = SellOrBuyType.BUY;

        List<ExecOwnershipDetailDTO.TopStockDetailDTO> buyDetails = Arrays.asList(
                ExecOwnershipDetailDTO.TopStockDetailDTO.builder()
                        .corpCode("00126380")
                        .corpName("삼성전자")
                        .totalStockAmount(1000000L)
                        .build()
        );

        given(execOwnershipDetailRepo.getTopStockDetail(any())).willReturn(buyDetails);

        // When: BUY만 조회
        List<ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse> result =
                execOwnershipService.getTopStockTradeTotal(tradeDtGoe, tradeDtLoe, sellOrBuyType);

        // Then: BUY 결과만 반환
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSellOrBuyType()).isEqualTo(SellOrBuyType.BUY.getCode());
    }

    @Test
    @DisplayName("Top 거래 조회 - ALL (매수/매도 모두)")
    void getTopStockTradeTotal_All() {
        // Given: 매수/매도 모두 조회
        String tradeDtGoe = "20240101";
        String tradeDtLoe = "20240131";
        SellOrBuyType sellOrBuyType = SellOrBuyType.ALL;

        List<ExecOwnershipDetailDTO.TopStockDetailDTO> details = Arrays.asList(
                ExecOwnershipDetailDTO.TopStockDetailDTO.builder()
                        .corpCode("00126380")
                        .corpName("삼성전자")
                        .totalStockAmount(1000000L)
                        .build()
        );

        given(execOwnershipDetailRepo.getTopStockDetail(any())).willReturn(details);

        // When: ALL 조회
        List<ExecOwnershipDetailDTO.SellOrBuyTop5StockResponse> result =
                execOwnershipService.getTopStockTradeTotal(tradeDtGoe, tradeDtLoe, sellOrBuyType);

        // Then: BUY와 SELL 두 개 모두 반환
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSellOrBuyType()).isEqualTo(SellOrBuyType.BUY.getCode());
        assertThat(result.get(1).getSellOrBuyType()).isEqualTo(SellOrBuyType.SELL.getCode());
    }

    @Test
    @DisplayName("임원 소유 거래 리스트 조회 - 성공")
    void getExecOwnershipTradeList_Success() {
        // Given: 조회 조건
        ExecOwnershipDetailSearchConditionReqDTO condition = ExecOwnershipDetailSearchConditionReqDTO.builder()
                .corpCodeEq("00126380")
                .build();

        ExecOwnershipDetail detail = mock(ExecOwnershipDetail.class);

        given(execOwnershipDetailRepo.getExecOwnershipDetailList(condition))
                .willReturn(Arrays.asList(detail));

        // When: 거래 리스트 조회
        List<ExecOwnershipDetail> result = execOwnershipService.getExecOwnershipTradeList(condition);

        // Then: 검증
        assertThat(result).hasSize(1);
        verify(execOwnershipDetailRepo).getExecOwnershipDetailList(condition);
    }
}
