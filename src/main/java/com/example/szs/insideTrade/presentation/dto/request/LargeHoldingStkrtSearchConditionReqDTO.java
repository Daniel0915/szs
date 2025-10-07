package com.example.szs.insideTrade.presentation.dto.request;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@FieldNameConstants
public class LargeHoldingStkrtSearchConditionReqDTO {
    private String orderColumn;
    private Boolean isDescending;
    private String corpCode;
    private Long limit;
}
