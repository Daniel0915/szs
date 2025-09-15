package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsRepo;
import com.example.szs.insideTrade.domain.QLargeHoldings;
import com.example.szs.utils.batch.JdbcBatchUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.szs.insideTrade.domain.QLargeHoldings.largeHoldings;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsJpaOrQueryDSLRepo implements LargeHoldingsRepo {
    private final ILargeHoldingsJpaRepo iLargeHoldingsJpaRepo;
    private final JPAQueryFactory       queryFactory;
    private final JdbcTemplate          jdbcTemplate;

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
        return iLargeHoldingsJpaRepo.saveAll(largeHoldings);
    }

    @Override
    public void insertNativeBatch(List<LargeHoldings> largeHoldings, int batchSize) throws Exception {
        // TODO : 테스트 코드
        log.info("jdbcTemplate 메모리 위치 확인, jdbcTemplate : {}", jdbcTemplate);
        // TODO : 테스트 코드

        String sql = """
                INSERT INTO large_holdings
                    (rcept_no, corp_code, corp_name, repror, stkqy, stkqy_irds, stkrt, stkrt_irds, report_resn, rcept_dt, reg_dt)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;


        JdbcBatchUtil.executeBatch(jdbcTemplate, largeHoldings, batchSize, sql, (ps, largeHolding) -> {
            ps.setString(1, largeHolding.getRceptNo());
            ps.setString(2, largeHolding.getCorpCode());
            ps.setString(3, largeHolding.getCorpName());
            ps.setString(4, largeHolding.getRepror());
            ps.setObject(5, largeHolding.getStkqy());
            ps.setObject(6, largeHolding.getStkqyIrds());
            ps.setObject(7, largeHolding.getStkrt());
            ps.setObject(8, largeHolding.getStkrtIrds());
            ps.setString(9, largeHolding.getReportResn());
            ps.setString(10, largeHolding.getRceptDt());
            ps.setString(11, largeHolding.getRegDt());
        });
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
