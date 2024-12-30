package com.example.szs.model.dto;

import com.example.szs.config.json.NullToEmptySerializer;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LargeHoldingsDTO {
    private String rceptNo; // 접수 번호
    private Long corpCode; // 회사코드
    private String corpName; // 회사명
    private String repror; // 보고자
    private Long stkqy; // 보유 주식수
    private Long stkqyIrds; // 보유주식 증감
    private Float stkrt; // 보유 비율
    private Float stkrtIrds; // 보유 비율 증감
    private String reportResn; // 보고 사유
    private String rceptDt; // 접수 일자
    private String regDt;
}
