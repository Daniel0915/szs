package com.example.szs.insideTrade.domain;


import com.example.szs.insideTrade.infrastructure.client.dto.ExecOwnershipInsiderTradeApiRes;
import com.example.szs.common.utils.money.NumberUtils;
import com.example.szs.common.utils.time.TimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "exec_ownership")
@Getter
@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExecOwnership {

    @Id
    @Column(name = "rcept_no")
    private String rceptNo; // 접수 번호

    @Column(name = "corp_code")
    private String corpCode; // 회사코드

    @Column(name = "corp_name")
    private String corpName; // 회사명

    @Column(name = "repror")
    private String repror; // 보고자

    @Column(name = "isu_exctv_rgist_at")
    private String isuExctvRgistAt; // 등기여부

    @Column(name = "isu_exctv_ofcps")
    private String isuExctvOfcps; // 직위

    @Column(name = "isu_main_shrholdr")
    private String isuMainShrholdr; // 주요 주주

    @Column(name = "sp_stock_lmp_cnt")
    private Long spStockLmpCnt; // 소유 주식 수

    @Column(name = "sp_stock_lmp_irds_cnt")
    private Long spStockLmpIrdsCnt; // 소유 증감수

    @Column(name = "sp_stock_lmp_rate")
    private Float spStockLmpRate; // 특정 증권 등 소유비율

    @Column(name = "sp_stock_lmp_irds_rate")
    private Float spStockLmpIrdsRate; // 특정 증권 등 소유 증감 비율

    @Column(name = "rcept_dt")
    private String rceptDt; // 접수 일자

    @Column(name = "reg_dt")
    private String regDt;

    private ExecOwnership(String rceptNo,
                          String corpCode,
                          String corpName,
                          String repror,
                          String isuExctvRgistAt,
                          String isuExctvOfcps,
                          String isuMainShrholdr,
                          Long spStockLmpCnt,
                          Long spStockLmpIrdsCnt,
                          Float spStockLmpRate,
                          Float spStockLmpIrdsRate) {
        this.rceptNo = rceptNo;
        this.corpCode = corpCode;
        this.corpName = corpName;
        this.repror = repror;
        this.isuExctvRgistAt = isuExctvRgistAt;
        this.isuExctvOfcps = isuExctvOfcps;
        this.isuMainShrholdr = isuMainShrholdr;
        this.spStockLmpCnt = spStockLmpCnt;
        this.spStockLmpIrdsCnt = spStockLmpIrdsCnt;
        this.spStockLmpRate = spStockLmpRate;
        this.spStockLmpIrdsRate = spStockLmpIrdsRate;
        this.regDt = TimeUtil.nowTime("yyyyMMddHHmmss");
    }

    public static ExecOwnership create(ExecOwnershipInsiderTradeApiRes.ExecOwnership execOwnership) {
        return new ExecOwnership(
                execOwnership.getRceptNo(),
                execOwnership.getCorpCode(),
                execOwnership.getCorpName(),
                execOwnership.getRepror(),
                execOwnership.getIsuExctvRgistAt(),
                execOwnership.getIsuExctvOfcps(),
                execOwnership.getIsuMainShrholdr(),
                NumberUtils.stringToLongConverter(execOwnership.getSpStockLmpCnt()),
                NumberUtils.stringToLongConverter(execOwnership.getSpStockLmpIrdsCnt()),
                NumberUtils.stringToFloatConverter(execOwnership.getSpStockLmpIrdsCnt()),
                NumberUtils.stringToFloatConverter(execOwnership.getSpStockLmpIrdsRate())
                );
    }
}
