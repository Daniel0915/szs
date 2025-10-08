package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.UserPushSubs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserPushSubsJpaRepo extends JpaRepository<UserPushSubs, Long> {
}
