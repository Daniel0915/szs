package com.example.szs.model.dto;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LargeHoldingsDTO {
    private String rceptNo; // 접수 번호
    private Long corpCode; // 회사코드
    private String corpName; // 회사명
    private String repror; // 보고자
    private Long stkqy; // 보유 주식수
    private Long stkqyIrds; // 보유주식 증감
    private Float stkrt; // 보유 비율
    private Float stkrtIrds; // 보유 비율 증감
    private String reportResn; // 보고 사유
    private String rceptDt; // 접수 일자
    private String regDt;

    public static String getMessage(String corpName, List<LargeHoldingsDTO> largeHoldingsDTOList) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("[").append(corpName).append("]").append("\n");
        for (LargeHoldingsDTO dto : largeHoldingsDTOList) {
            messageBuilder.append("보고자 : ").append(dto.getRepror()).append("\n");
            messageBuilder.append("현재 소유 주식 : ").append(dto.getStkqy()).append("\n");
            if (dto.getStkqyIrds() < 0) {
                messageBuilder.append("주식을 팔았어요. 매수 주식 수 : ").append(Math.abs(dto.getStkqyIrds())).append("\n");
            } else {
                messageBuilder.append("주식을 샀어요. 매도 주식 수: ").append(Math.abs(dto.getStkqyIrds())).append("\n");
            }
            messageBuilder.append("지분 변경 날짜 : ").append(dto.getRepror()).append("\n");
            messageBuilder.append("===================================").append("\n");
        }

        return messageBuilder.toString();
    }
}
