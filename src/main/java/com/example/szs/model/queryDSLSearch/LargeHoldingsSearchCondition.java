package com.example.szs.model.queryDSLSearch;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LargeHoldingsSearchCondition {
    private String orderColumn;
    private boolean isDescending;

    // where 절 사용
    private String rceptNo;
    private Long corpCode;
}
