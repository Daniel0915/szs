package com.example.szs.insideTrade.infrastructure.client.dto;

import com.example.szs.model.dto.execOwnership.EOResponseDTO;
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
public class ExecOwnershipInsiderTradeApiRes {
    private String              status;
    private String              message;
    private List<ExecOwnership> list;

    @Getter
    @NoArgsConstructor
    public static class ExecOwnership {
        @JsonProperty("rcept_no")
        private String rceptNo; // 접수 번호

        @JsonProperty("corp_code")
        private String corpCode; // 회사코드

        @JsonProperty("corp_name")
        private String corpName; // 회사명

        @JsonProperty("repror")
        private String repror; // 보고자

        @JsonProperty("isu_exctv_rgist_at")
        private String isuExctvRgistAt; // 등기여부

        @JsonProperty("isu_exctv_ofcps")
        private String isuExctvOfcps; // 직위

        @JsonProperty("isu_main_shrholdr")
        private String isuMainShrholdr; // 주요 주주

        @JsonProperty("sp_stock_lmp_cnt")
        private String spStockLmpCnt; // 소유 주식 수

        @JsonProperty("sp_stock_lmp_irds_cnt")
        private String spStockLmpIrdsCnt; // 소유 증감수

        @JsonProperty("sp_stock_lmp_rate")
        private String spStockLmpRate; // 특정 증권 등 소유비율

        @JsonProperty("sp_stock_lmp_irds_rate")
        private String spStockLmpIrdsRate; // 특정 증권 등 소유 증감 비율

        @JsonProperty("rcept_dt")
        private String rceptDt; // 접수 일자
    }
}
