package com.example.szs.insideTrade.infrastructure.db.queryDSL;

import com.example.szs.insideTrade.domain.LargeHoldingsDetail;
import com.example.szs.insideTrade.domain.LargeHoldingsDetailRepo;
import com.example.szs.insideTrade.infrastructure.db.jpa.ILargeHoldingsDetailJpaRepo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsDetailQueryDSLRepo implements LargeHoldingsDetailRepo {
    private final JPAQueryFactory             queryFactory;
    private final ILargeHoldingsDetailJpaRepo iLargeHoldingsDetailJpaRepo;

    @Override
    public List<LargeHoldingsDetail> saveAll(List<LargeHoldingsDetail> entities) {
        return iLargeHoldingsDetailJpaRepo.saveAll(entities);
    }
}
