package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.UserPushSubs;
import com.example.szs.insideTrade.domain.UserPushSubsRepo;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.queryDSLSearch.UserPushSubsSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.szs.insideTrade.domain.QUserPushSubs.userPushSubs;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserPushSubsJpaOrQueryDSLRepo implements UserPushSubsRepo {
    private final IUserPushSubsJpaRepo iUserPushSubsJpaRepo;
    private final JPAQueryFactory      queryFactory;

    public List<UserPushSubs> getUserPushSubsListBy(UserPushSubsSearchCondition condition) {
        return queryFactory.selectFrom(userPushSubs)
                           .where(
                                   seqEq(condition.getSeq()),
                                   userIdEq(condition.getUserId()),
                                   channelTypeEq(condition.getChannelType())
                           )
                           .fetch();
    }

    private BooleanExpression seqEq(Long seq) {
        return seq != null && seq != 0L ? userPushSubs.seq.eq(seq) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null && userId != 0L ? userPushSubs.userId.eq(userId) : null;
    }

    private BooleanExpression channelTypeEq(ChannelType channelType) {
        return channelType != null ? userPushSubs.channelType.eq(channelType.name()) : null;
    }
}
