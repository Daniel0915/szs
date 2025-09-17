package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.ExecOwnershipDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IExecOwnershipDetailJpaRepo extends JpaRepository<ExecOwnershipDetail, Long> {
}
