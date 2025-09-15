package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.LargeHoldings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ILargeHoldingsJpaRepo extends JpaRepository<LargeHoldings, Long> {
    @Override
    Optional<LargeHoldings> findById(Long rceptNo);
}
