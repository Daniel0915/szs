package com.example.szs.repository.stock;

import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
import com.example.szs.model.queryDSLSearch.LargeHoldingsSearchCondition;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.example.szs.domain.stock.LargeHoldingsEntity.Fields.stkqy;

import org.assertj.core.api.Assertions;

@SpringBootTest
@Transactional
class LargeHoldingsRepositoryCustomTest {
    @Autowired
    EntityManager em;

    @Autowired
    LargeHoldingsRepositoryCustom largeHoldingsRepositoryCustom;

    @Test
    void findLatestRecordBy() {
        LargeHoldingsSearchCondition condition = LargeHoldingsSearchCondition.builder()
                                                                             .corpCode(126380L)
                                                                             .orderColumn(stkqy)
                                                                             .isDescending(true)
                                                                             .build();

        LargeHoldingsDTO largeHoldingsDTO = largeHoldingsRepositoryCustom.findLatestRecordBy(condition).get();

        Assertions.assertThat(largeHoldingsDTO.getRceptNo()).isEqualTo("20210924000389");
    }
}