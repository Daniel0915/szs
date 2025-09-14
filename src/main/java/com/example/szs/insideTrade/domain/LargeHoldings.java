package com.example.szs.insideTrade.domain;

import com.example.szs.utils.time.TimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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

    // TODO : 매개변수가 길어서, 차라리 메서드 명칭을 변경 && 매개변수를 객체로 변경
    public static LargeHoldings create(String rceptNo, String corpCode, String corpName, String repror, Long stkqy, Long stkqyIrds, Float stkrt, Float stkrtIrds, String reportResn, String rceptDt) {
        return new LargeHoldings(rceptNo, corpCode, corpName, repror, stkqy, stkqyIrds, stkrt, stkrtIrds, reportResn, rceptDt);
    }
}
