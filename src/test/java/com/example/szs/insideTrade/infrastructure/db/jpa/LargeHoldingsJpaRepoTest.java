package com.example.szs.insideTrade.infrastructure.db.jpa;

import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL.ILargeHoldingsJpaRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(LargeHoldingsJpaRepo.class)
@ActiveProfiles("test")
class LargeHoldingsJpaRepoTest {
    @Autowired
    LargeHoldingsJpaRepo repo;

    @Autowired
    ILargeHoldingsJpaRepo iLargeHoldingsJpaRepo; // 실제 JPA 레포

    @Test
    void saveAll_테스트() {
        // TODO : 테스트 코드 작성하기
        LargeHoldings h1 = LargeHoldings.create("R001", "C001", "삼성전자", "홍길동",
                1000L, 200L, 5.5f, 0.3f, "취득", "20250912");
        List<LargeHoldings> insertList = List.of(h1);

        List<LargeHoldings> result = repo.saveAll(insertList);

        System.out.println(result);
    }

}