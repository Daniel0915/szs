package com.example.szs.insideTrade.presentation.dto.request;

import lombok.*;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExecOwnershipSearchCondition {
    private String orderColumn;
    private boolean isDescending;

    // where 절 사용
    private String rceptNo;
    private String corpCode;
}
