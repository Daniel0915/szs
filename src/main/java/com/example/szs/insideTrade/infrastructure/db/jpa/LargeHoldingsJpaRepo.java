package com.example.szs.insideTrade.infrastructure.db.jpa;

import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsRepo;
import com.example.szs.insideTrade.infrastructure.db.queryDSL.LargeHoldingsSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsJpaRepo implements LargeHoldingsRepo {
    private final ILargeHoldingsJpaRepo iLargeHoldingsJpaRepo;

    @Override
    public Optional<LargeHoldings> findLatestRecordBy(LargeHoldingsSearchCondition searchCondition) {
        // TODO : 필요 시, 작성 필요
        return Optional.empty();
    }

    @Override
    public List<LargeHoldings> saveAll(List<LargeHoldings> largeHoldings) {
        return iLargeHoldingsJpaRepo.saveAll(largeHoldings);
    }
}
