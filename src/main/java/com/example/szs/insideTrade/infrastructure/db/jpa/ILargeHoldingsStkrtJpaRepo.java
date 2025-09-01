package com.example.szs.insideTrade.infrastructure.db.jpa;

import com.example.szs.insideTrade.domain.LargeHoldingsStkrt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILargeHoldingsStkrtJpaRepo extends JpaRepository<LargeHoldingsStkrt, Long> {
}
