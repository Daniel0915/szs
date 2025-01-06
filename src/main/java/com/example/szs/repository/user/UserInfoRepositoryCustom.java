package com.example.szs.repository.user;

import com.example.szs.domain.subscribe.UserPushSubs;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class UserInfoRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserInfoRepositoryCustom(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }
}
