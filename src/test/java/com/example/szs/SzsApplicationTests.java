package com.example.szs;

import com.example.szs.domain.stock.LargeHoldingsEntity;
import com.example.szs.domain.stock.QLargeHoldingsEntity;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SzsApplicationTests {

    @Autowired
    EntityManager em;

    @Test
    void contextLoads() {
//        LargeHoldingsEntity largeHoldingsEntity = LargeHoldingsEntity.builder()
//                                                                     .rceptNo("!212")
//                                                                     .corpCode("1212")
//                                                                     .build();
//
//        em.persist(largeHoldingsEntity);
//        JPAQueryFactory query = new JPAQueryFactory(em);
//        QLargeHoldingsEntity qLargeHoldingsEntity = new QLargeHoldingsEntity("l");
//
//        LargeHoldingsEntity result = query.selectFrom(qLargeHoldingsEntity)
//                                          .where(qLargeHoldingsEntity.rceptNo.eq("!212"))
//                                          .fetchOne();
//
//
//        Assertions.assertThat(result).isEqualTo(largeHoldingsEntity);
    }

}
