package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.CorpInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICorpInfoJpaRepo extends JpaRepository<CorpInfo, Long> {
}
