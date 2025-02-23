package com.example.szs.model.dto.largeHoldings;

import com.example.szs.domain.stock.LargeHoldingsEntity;
import com.example.szs.utils.money.NumberUtils;
import com.example.szs.utils.time.TimeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LHResponseDTO {
    private String status;
    private String message;
    private List<LargeHolding> list;

    @Getter
    @NoArgsConstructor
    private static class LargeHolding {
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

    public List<LargeHoldingsEntity> toEntity() {
        List<LargeHoldingsEntity> largeHoldingsEntityList = new ArrayList<>();
        for (LargeHolding largeHolding : this.list) {
            largeHoldingsEntityList.add(LargeHoldingsEntity.builder()
                                                           .rceptNo     (largeHolding.getRceptNo())
                                                           .corpCode    (NumberUtils.stringToLongConverter(largeHolding.getCorpCode()))
                                                           .corpName    (largeHolding.getCorpName())
                                                           .repror      (largeHolding.getRepror())
                                                           .stkqy       (NumberUtils.stringToLongConverter(largeHolding.getStkqy()))
                                                           .stkqyIrds   (NumberUtils.stringToLongConverter(largeHolding.getStkqyIrds()))
                                                           .stkrt       (Float.valueOf(largeHolding.getStkrt()))
                                                           .stkrtIrds   (Float.valueOf(largeHolding.getStkrtIrds()))
                                                           .reportResn  (largeHolding.getReportResn())
                                                           .rceptDt     (largeHolding.getRceptDt())
                                                           .regDt       (TimeUtil.nowTime("yyyyMMddHHmmss"))
                                                           .build()
            );
        }

        return largeHoldingsEntityList;
    }
}
