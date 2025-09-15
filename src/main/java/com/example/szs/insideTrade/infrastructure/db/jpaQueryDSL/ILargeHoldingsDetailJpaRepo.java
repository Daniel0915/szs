package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.LargeHoldingsDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILargeHoldingsDetailJpaRepo extends JpaRepository<LargeHoldingsDetail, Long> {
}
