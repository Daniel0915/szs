package com.example.szs.insideTrade.infrastructure.db.queryDSL;

import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsRepo;
import com.example.szs.insideTrade.domain.QLargeHoldings;
import com.example.szs.insideTrade.infrastructure.db.jpa.ILargeHoldingsJpaRepo;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.szs.insideTrade.domain.QLargeHoldings.largeHoldings;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsQueryDSLRepo implements LargeHoldingsRepo {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<LargeHoldings> findLatestRecordBy(LargeHoldingsSearchCondition condition) {
        return Optional.ofNullable(queryFactory.selectFrom(largeHoldings)
                                               .where(
                                                       rceptNoEq(condition.getRceptNo()),
                                                       corpCodeEq(condition.getCorpCode())
                                               )
                                               .orderBy(dynamicOrder(condition))
                                               .fetchFirst());
    }

    @Override
    public List<LargeHoldings> saveAll(List<LargeHoldings> largeHoldings) {
        assert false : "사용하지 마세요. JPA 에서 처리됩니다.";
        return new ArrayList<>();
    }

    @Override
    public void insertNativeBatch(List<LargeHoldings> largeHoldings, int batchSize) {
        assert false : "사용하지 마세요. JPA 에서 처리됩니다.";
    }

    private BooleanExpression rceptNoEq(String rceptNo) {
        return hasText(rceptNo) ? largeHoldings.rceptNo.eq(rceptNo) : null;
    }

    private BooleanExpression corpCodeEq(String corpCode) {
        return StringUtils.hasText(corpCode) ? largeHoldings.corpCode.eq(corpCode) : null;
    }

    private OrderSpecifier<?> dynamicOrder(LargeHoldingsSearchCondition condition) {
        if (!hasText(condition.getOrderColumn())) {
            return largeHoldings.rceptNo.asc();
        }

        String orderColumn = condition.getOrderColumn();
        boolean isDescending = condition.isDescending();

        Class<?> clazz = QLargeHoldings.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(largeHoldings.getType(), largeHoldings.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return largeHoldings.rceptNo.asc();
    }
}
