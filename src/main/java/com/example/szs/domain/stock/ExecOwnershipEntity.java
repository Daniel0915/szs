package com.example.szs.domain.stock;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exec_ownership")
@Getter
@FieldNameConstants
public class ExecOwnershipEntity {

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
}
