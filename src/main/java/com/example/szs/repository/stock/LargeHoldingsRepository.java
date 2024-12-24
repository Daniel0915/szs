package com.example.szs.repository.stock;

import com.example.szs.domain.stock.LargeHoldingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LargeHoldingsRepository extends JpaRepository<LargeHoldingsEntity,Long> {
}
