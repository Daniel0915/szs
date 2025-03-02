package com.example.szs.domain.stock;

import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "large_holdings")
@Getter
@FieldNameConstants
public class LargeHoldingsEntity {
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
}
