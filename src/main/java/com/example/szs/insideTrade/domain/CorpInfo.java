package com.example.szs.insideTrade.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "corp_info")
@Getter
@FieldNameConstants
public class CorpInfo {
    @Id
    @Column(name = "corp_code")
    private String corpCode; // 회사코드

    @Column(name = "corp_name")
    private String corpName; // 회사명

    @Column(name = "reg_dt")
    private String regDt;
}
