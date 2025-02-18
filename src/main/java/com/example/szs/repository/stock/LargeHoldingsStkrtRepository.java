package com.example.szs.repository.stock;

import com.example.szs.domain.stock.LargeHoldingsStkrtEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LargeHoldingsStkrtRepository extends JpaRepository<LargeHoldingsStkrtEntity, Long> {
}