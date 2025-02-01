package com.example.szs.repository.stock;

import com.example.szs.domain.stock.LargeHoldingsDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LargeHoldingsDetailRepository extends JpaRepository<LargeHoldingsDetailEntity, Long> {
}
