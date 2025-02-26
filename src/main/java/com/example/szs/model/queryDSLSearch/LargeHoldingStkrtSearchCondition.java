package com.example.szs.model.queryDSLSearch;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@FieldNameConstants
public class LargeHoldingStkrtSearchCondition {
    private String orderColumn;
    private Boolean isDescending;
    private String corpCode;
    private Long limit;
}
