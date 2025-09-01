package com.example.szs.insideTrade.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LargeHoldingsInsiderTradeApiRes {
    private String             status;
    private String             message;
    private List<LargeHolding> list;

    @Getter
    @NoArgsConstructor
    public static class LargeHolding {
        @JsonProperty("rcept_no")
        private String rceptNo; // 접수 번호

        @JsonProperty("corp_code")
        private String corpCode; // 회사코드

        @JsonProperty("corp_name")
        private String corpName; // 회사명

        @JsonProperty("repror")
        private String repror; // 보고자

        @JsonProperty("stkqy")
        private String stkqy; // 보유 주식수

        @JsonProperty("stkqy_irds")
        private String stkqyIrds; // 보유주식 증감

        @JsonProperty("stkrt")
        private String stkrt; // 보유 비율

        @JsonProperty("stkrt_irds")
        private String stkrtIrds; // 보유 비율 증감

        @JsonProperty("report_resn")
        private String reportResn; // 보고 사유

        @JsonProperty("rcept_dt")
        private String rceptDt; // 접수 일자
    }
}
