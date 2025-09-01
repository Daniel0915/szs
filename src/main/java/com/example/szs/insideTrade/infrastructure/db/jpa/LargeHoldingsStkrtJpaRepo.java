package com.example.szs.insideTrade.infrastructure.db.jpa;

import com.example.szs.insideTrade.domain.LargeHoldingsStkrt;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrtRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsStkrtJpaRepo implements LargeHoldingsStkrtRepo {
    private final ILargeHoldingsStkrtJpaRepo iLargeHoldingsStkrtJpaRepo;

    @Override
    public List<LargeHoldingsStkrt> saveAll(List<LargeHoldingsStkrt> entities) {
        return iLargeHoldingsStkrtJpaRepo.saveAll(entities);
    }
}
