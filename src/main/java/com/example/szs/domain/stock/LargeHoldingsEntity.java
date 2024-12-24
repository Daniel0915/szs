package com.example.szs.domain.stock;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "LARGE_HOLDINGS")
@Getter
public class LargeHoldingsEntity {
    @Id @GeneratedValue
    Long rceptNo;
    String corpCode;
    String corpName;
    String repror;
    Long stkqy;
    Long stkqyIrds;
    String reportResn;
    String rceptDt;
    String regDt;
}
