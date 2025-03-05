package com.example.szs.model.dto.execOwnership;

import com.example.szs.domain.stock.ExecOwnershipEntity;
import com.example.szs.utils.money.NumberUtils;
import com.example.szs.utils.time.TimeUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EOResponseDTO {
    private String status;
    private String message;
    private List<ExecOwnership> list;

    @Getter
    @NoArgsConstructor
    private static class ExecOwnership {
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

    public List<ExecOwnershipEntity> toEntity() {
        List<ExecOwnershipEntity> execOwnershipEntityList = new ArrayList<>();

        if (CollectionUtils.isEmpty(this.list)) {
            return new ArrayList<>();
        }

        for (ExecOwnership execOwnership : this.list) {
            execOwnershipEntityList.add(ExecOwnershipEntity.builder()
                                                           .rceptNo             (execOwnership.getRceptNo())
                                                           .corpCode            (execOwnership.getCorpCode())
                                                           .corpName            (execOwnership.getCorpName())
                                                           .repror              (execOwnership.getRepror())
                                                           .isuExctvRgistAt     (execOwnership.getIsuExctvRgistAt())
                                                           .isuExctvOfcps       (execOwnership.getIsuExctvOfcps())
                                                           .isuMainShrholdr     (execOwnership.getIsuMainShrholdr())
                                                           .spStockLmpCnt       (NumberUtils.stringToLongConverter(execOwnership.getSpStockLmpCnt()))
                                                           .spStockLmpIrdsCnt   (NumberUtils.stringToLongConverter(execOwnership.getSpStockLmpIrdsCnt()))
                                                           .spStockLmpRate      (Float.valueOf(execOwnership.getSpStockLmpRate()))
                                                           .spStockLmpIrdsRate  (Float.valueOf(execOwnership.getSpStockLmpIrdsRate()))
                                                           .rceptDt             (execOwnership.getRceptDt())
                                                           .regDt               (TimeUtil.nowTime("yyyyMMddHHmmss"))
                                                           .build());
        }

        return execOwnershipEntityList;
    }
}
