package com.example.szs.repository.subscribe;

import com.example.szs.domain.subscribe.UserPushSubs;
import com.example.szs.model.dto.subscribe.UserPushSubsDTO;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.queryDSLSearch.UserPushSubsSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static com.example.szs.domain.subscribe.QUserPushSubs.userPushSubs;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserPushSubsRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public UserPushSubsRepositoryCustom(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<UserPushSubsDTO> getUserPushSubsDTOListBy(UserPushSubsSearchCondition condition) {
        return queryFactory.selectFrom(userPushSubs)
                           .where(
                                   seqEq(condition.getSeq()),
                                   userIdEq(condition.getUserId()),
                                   channelTypeEq(condition.getChannelType())
                           )
                           .fetch()
                           .stream()
                           .map(UserPushSubs::toDTO)
                           .collect(Collectors.toList());
    }

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
        return userId != null && userId != 0L ? userPushSubs.userInfo.userId.eq(userId) : null;
    }

    private BooleanExpression channelTypeEq(ChannelType channelType) {
        return channelType != null ? userPushSubs.channelType.eq(channelType.name()) : null;
    }
}
