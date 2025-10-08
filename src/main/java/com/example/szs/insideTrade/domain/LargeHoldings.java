package com.example.szs.insideTrade.domain;

import com.example.szs.common.utils.money.NumberUtils;
import com.example.szs.common.utils.time.TimeUtil;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsInsiderTradeApiRes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "large_holdings")
@Getter
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LargeHoldings {
    @Id
    @Column(name = "rcept_no")
    private String rceptNo; // 접수 번호

    @Column(name = "corp_code")
    private String corpCode; // 회사코드

    @Column(name = "corp_name")
    private String corpName; // 회사명

    @Column(name = "repror")
    private String repror; // 보고자

    @Column(name = "stkqy")
    private Long stkqy; // 보유 주식수

    @Column(name = "stkqy_irds")
    private Long stkqyIrds; // 보유주식 증감

    @Column(name = "stkrt")
    private Float stkrt; // 보유 비율

    @Column(name = "stkrt_irds")
    private Float stkrtIrds; // 보유 비율 증감

    @Column(name = "report_resn")
    private String reportResn; // 보고 사유

    @Column(name = "rcept_dt")
    private String rceptDt; // 접수 일자

    @Column(name = "reg_dt")
    private String regDt;

    @Builder
    private LargeHoldings(String rceptNo, String corpCode, String corpName, String repror, Long stkqy, Long stkqyIrds, Float stkrt, Float stkrtIrds, String reportResn, String rceptDt) {
        this.rceptNo = rceptNo;
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.repror = repror;
        this.stkqy = stkqy;
        this.stkqyIrds = stkqyIrds;
        this.stkrt = stkrt;
        this.stkrtIrds = stkrtIrds;
        this.reportResn = reportResn;
        this.rceptDt = rceptDt;
        this.regDt = TimeUtil.nowTime("yyyyMMddHHmmss");
    }

    public static LargeHoldings create(LargeHoldingsInsiderTradeApiRes.LargeHolding largeHolding) {
        return LargeHoldings.builder()
                            .rceptNo(largeHolding.getRceptNo())
                            .corpCode(largeHolding.getCorpCode())
                            .corpName(largeHolding.getCorpName())
                            .repror(largeHolding.getRepror())
                            .stkqy(NumberUtils.stringToLongConverter(largeHolding.getStkqy()))
                            .stkqyIrds(NumberUtils.stringToLongConverter(largeHolding.getStkqyIrds()))
                            .stkrt(NumberUtils.stringToFloatConverter(largeHolding.getStkrt()))
                            .stkrtIrds(NumberUtils.stringToFloatConverter(largeHolding.getStkrtIrds()))
                            .reportResn(largeHolding.getReportResn())
                            .rceptDt(largeHolding.getRceptDt())
                            .build();
    }
}
