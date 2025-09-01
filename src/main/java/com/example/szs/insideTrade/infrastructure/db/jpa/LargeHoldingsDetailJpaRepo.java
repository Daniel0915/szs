package com.example.szs.insideTrade.infrastructure.db.jpa;

import com.example.szs.insideTrade.domain.LargeHoldingsDetail;
import com.example.szs.insideTrade.domain.LargeHoldingsDetailRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsDetailJpaRepo implements LargeHoldingsDetailRepo {
    private final ILargeHoldingsDetailJpaRepo iLargeHoldingsDetailJpaRepo;

    @Override
    public List<LargeHoldingsDetail> saveAll(List<LargeHoldingsDetail> entities) {
        return iLargeHoldingsDetailJpaRepo.saveAll(entities);
    }
}
