package com.example.szs.insideTrade.infrastructure.db.queryDSL;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CorpInfoQueryDSLRepo implements CorpInfoRepo {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CorpInfo> findAll() {
        return List.of();
    }
}
