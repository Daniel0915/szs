package com.example.szs.insideTrade.application;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.example.szs.insideTrade.presentation.dto.response.CorpInfoResDTO;
import com.example.szs.service.stock.CorpInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * CorpInfoService 테스트
 * 이 테스트는 단위 테스트(Unit Test)입니다.
 * - 실제 DB 없이 Mock 객체를 사용해서 테스트합니다
 * - @ExtendWith(MockitoExtension.class): Mockito를 사용하기 위한 설정
 * - @Mock: 가짜 객체(Mock) 생성
 * - @InjectMocks: Mock 객체들을 주입받는 테스트 대상 객체
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CorpInfoService 단위 테스트")
class CorpInfoServiceTest {

    @Mock // 가짜 Repository 객체 (실제 DB에 접근하지 않음)
    private CorpInfoRepo corpInfoRepo;

    @InjectMocks // Mock 객체들을 주입받는 테스트 대상
    private CorpInfoService corpInfoService;

    @Test
    @DisplayName("모든 회사 정보 DTO 리스트 조회 - 성공")
    void getAllCorpInfoDTOList_Success() {
        // Given: 테스트 데이터 준비
        // 실제 DB 에서 반환될 것처럼 가상의 데이터를 준비합니다
        CorpInfo corp1 = CorpInfo.builder()
                .corpCode("00126380")
                .corpName("삼성전자")
                .build();

        CorpInfo corp2 = CorpInfo.builder()
                .corpCode("00164779")
                .corpName("SK하이닉스")
                .build();

        List<CorpInfo> mockCorpInfoList = Arrays.asList(corp1, corp2);

        // corpInfoRepo.findAll()이 호출되면 위에서 준비한 데이터를 반환하도록 설정
        given(corpInfoRepo.findAll()).willReturn(mockCorpInfoList);

        // When: 실제 테스트할 메서드 실행
        List<CorpInfoResDTO> result = corpInfoService.getAllCorpInfoDTOList();

        // Then: 결과 검증
        // 1. 결과가 null이 아닌지 확인
        assertThat(result).isNotNull();

        // 2. 결과 리스트의 크기가 2인지 확인
        assertThat(result).hasSize(2);

        // 3. 첫 번째 회사 정보 확인
        assertThat(result.get(0).getCorpCode()).isEqualTo("00126380");
        assertThat(result.get(0).getCorpName()).isEqualTo("삼성전자");

        // 4. 두 번째 회사 정보 확인
        assertThat(result.get(1).getCorpCode()).isEqualTo("00164779");
        assertThat(result.get(1).getCorpName()).isEqualTo("SK하이닉스");

        // 5. Repository의 findAll() 메서드가 정확히 1번 호출되었는지 확인
        verify(corpInfoRepo).findAll();
    }

    @Test
    @DisplayName("모든 회사 정보 DTO 리스트 조회 - 빈 리스트")
    void getAllCorpInfoDTOList_EmptyList() {
        // Given: 빈 리스트 반환 설정
        given(corpInfoRepo.findAll()).willReturn(Arrays.asList());

        // When: 메서드 실행
        List<CorpInfoResDTO> result = corpInfoService.getAllCorpInfoDTOList();

        // Then: 빈 리스트 확인
        assertThat(result).isEmpty();
        verify(corpInfoRepo).findAll();
    }
}