package com.example.szs;

import com.example.szs.domain.stock.LargeHoldingsEntity;
import com.example.szs.domain.stock.QLargeHoldingsEntity;
import com.example.szs.domain.subscribe.UserPushSubs;
import com.example.szs.model.dto.subscribe.UserPushSubsDTO;
import com.example.szs.model.eNum.redis.ChannelType;
import com.example.szs.model.queryDSLSearch.UserPushSubsSearchCondition;
import com.example.szs.repository.subscribe.UserPushSubsRepositoryCustom;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class SzsApplicationTests {

    @Autowired
    EntityManager em;

    @Autowired
    UserPushSubsRepositoryCustom userPushSubsRepositoryCustom;

    @Test
    void contextLoads() {
        Long seq = 1L;

        List<UserPushSubs> userPushSubsListTest_1 = userPushSubsRepositoryCustom.getUserPushSubsListBy(UserPushSubsSearchCondition.builder()
                                                                                                                                     .seq(1L)
                                                                                                                                     .build());
        System.out.println("userPushSubsListTest_1.get(0).getUserInfo().getUserId() = " + userPushSubsListTest_1.get(0)
                                                                                                                .getUserInfo()
                                                                                                                .getUserId());


        Assertions.assertThat(userPushSubsListTest_1.get(0).getSeq()).isEqualTo(seq);

//        List<UserPushSubsDTO> userPushSubsListTest_2 = userPushSubsRepositoryCustom.getUserPushSubsListBy(UserPushSubsSearchCondition.builder()
//                                                                                                                                .userId(1L)
//                                                                                                                                .build());
//
//        Assertions.assertThat(userPushSubsListTest_2.get(0).getSeq()).isEqualTo(seq);
//
//        List<UserPushSubsDTO> userPushSubsListTest_3 = userPushSubsRepositoryCustom.getUserPushSubsListBy(UserPushSubsSearchCondition.builder()
//                                                                                                                                  .channelType(ChannelType.STOCK_CHANGE_NOTIFY_LARGE_HOLDINGS)
//                                                                                                                                  .build());
//
//        Assertions.assertThat(userPushSubsListTest_3.get(0).getSeq()).isEqualTo(seq);
//
//
//        Assertions.assertThat(userPushSubsListTest_3.get(0).getUserInfo().getUserEmail()).isEqualTo("ufo9363@gmail.com");
    }

}
