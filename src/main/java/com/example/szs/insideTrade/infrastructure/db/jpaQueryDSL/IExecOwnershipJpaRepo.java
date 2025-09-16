package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.ExecOwnership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IExecOwnershipJpaRepo extends JpaRepository<ExecOwnership, String> {
}
