package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CorpInfoJpaQueryDSLRepo implements CorpInfoRepo {
    private final ICorpInfoJpaRepo iCorpInfoJpaRepo;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CorpInfo> findAll() {
        return iCorpInfoJpaRepo.findAll();
    }
}
