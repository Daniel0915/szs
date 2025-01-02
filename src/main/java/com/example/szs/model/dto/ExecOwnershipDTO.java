package com.example.szs.model.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String rceptDt; // 접수 일자
    private String regDt;

    public static String getMessage(String corpName, List<ExecOwnershipDTO> execOwnershipDTOList) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("[").append(corpName).append("]").append("\n");
        for (ExecOwnershipDTO dto : execOwnershipDTOList) {
            messageBuilder.append("보고자 : ").append(dto.getRepror()).append("\n");
            messageBuilder.append("등기여부 : ").append(dto.getIsuExctvRgistAt()).append("\n");
            messageBuilder.append("직위 : ").append(dto.getIsuExctvOfcps()).append("\n");
            messageBuilder.append("현재 소유 주식 : ").append(dto.getSpStockLmpCnt()).append("\n");
            if (dto.getSpStockLmpIrdsCnt() < 0) {
                messageBuilder.append("주식을 팔았어요. 매수 주식 수 : ").append(Math.abs(dto.getSpStockLmpIrdsCnt())).append("\n");
            } else {
                messageBuilder.append("주식을 샀어요. 매도 주식 수: ").append(Math.abs(dto.getSpStockLmpIrdsCnt())).append("\n");
            }
            messageBuilder.append("지분 변경 날짜 : ").append(dto.getRepror()).append("\n");
            messageBuilder.append("===================================").append("\n");
        }
        return messageBuilder.toString();
    }
}
