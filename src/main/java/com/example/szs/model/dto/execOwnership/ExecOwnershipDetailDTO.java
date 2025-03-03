package com.example.szs.model.dto.execOwnership;

import com.example.szs.config.json.NullToEmptySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = NullToEmptySerializer.class)
@FieldNameConstants
@ToString
public class ExecOwnershipDetailDTO {
    private Long seq;
    private String rceptNo; // 접수 번호
    private String corpCode; // 회사코드
    private String corpName; // 회사명
    private String execOwnershipName; // 임원 주주 이름
    private String isuExctvRgistAt; // 등기여부
    private String isuExctvOfcps; // 직위
    private String isuMainShrholdr; // 주요 주주
    private String tradeDt; // 거래일
    private String tradeReason; // 매매 사유
    private String stockType; // 주식 종류
    private Long beforeStockAmount; // 주식 변동전
    private Long changeStockAmount; // 주식 증감
    private Long afterStockAmount; // 주식 변동후
    private Long unitStockPrice; // 주식 단가
    private Long totalStockPrice; // 전체 주식 취득 / 처분 단가
    private String regDt;
}
