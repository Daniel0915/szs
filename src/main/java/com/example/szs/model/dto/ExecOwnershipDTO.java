package com.example.szs.model.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExecOwnershipDTO {
    private String rceptNo; // 접수 번호
    private Long corpCode; // 회사코드
    private String corpName; // 회사명
    private String repror; // 보고자
    private String isuExctvRgistAt; // 등기여부
    private String isuExctvOfcps; // 직위
    private String isuMainShrholdr; // 주요 주주
    private Long spStockLmpCnt; // 소유 주식 수
    private Long spStockLmpIrdsCnt; // 소유 증감수
    private Float spStockLmpRate; // 특정 증권 등 소유비율
    private Float spStockLmpIrdsRate; // 특정 증권 등 소유 증감 비율
    private String regDt;
}
