package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.LargeHoldingsStkrt;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrtRepo;
import com.example.szs.insideTrade.domain.QLargeHoldingsStkrt;
import com.example.szs.insideTrade.presentation.dto.request.LargeHoldingStkrtSearchConditionReqDTO;
import com.example.szs.insideTrade.presentation.dto.response.LargeHoldingsStkrtResDTO;
import com.example.szs.utils.batch.JdbcBatchUtil;
import com.example.szs.utils.jpa.EntityToDtoMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.szs.insideTrade.domain.QLargeHoldingsStkrt.largeHoldingsStkrt;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsStkrtJpaRepo implements LargeHoldingsStkrtRepo {
    private final ILargeHoldingsStkrtJpaRepo iLargeHoldingsStkrtJpaRepo;
    private final JdbcTemplate    jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<LargeHoldingsStkrt> saveAll(List<LargeHoldingsStkrt> entities) {
        return iLargeHoldingsStkrtJpaRepo.saveAll(entities);
    }

    @Override
    public void insertNativeBatch(List<LargeHoldingsStkrt> entities, int batchSize) {
        String sql = """
            INSERT INTO large_holdings_stkrt
                (rcept_no, corp_code, corp_name, large_holdings_name, birth_date_or_biz_reg_num,
                 total_stock_amount, stkrt, reg_dt)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        JdbcBatchUtil.executeBatch(jdbcTemplate, entities, batchSize, sql, (ps, entity) -> {
            ps.setString(1, entity.getRceptNo());
            ps.setString(2, entity.getCorpCode());
            ps.setString(3, entity.getCorpName());
            ps.setString(4, entity.getLargeHoldingsName());
            ps.setString(5, entity.getBirthDateOrBizRegNum());
            ps.setObject(6, entity.getTotalStockAmount());
            ps.setObject(7, entity.getStkrt());
            ps.setString(8, entity.getRegDt());
        });
    }

    @Override
    public List<LargeHoldingsStkrt> getLargeHoldingsStockRatio(LargeHoldingStkrtSearchConditionReqDTO condition) {
        if (condition.getCorpCode() == null) return new ArrayList<>();

        // 가장 최근 등록일
        Optional<LargeHoldingsStkrt> findOneByRecentRecptNoDesc = Optional.ofNullable(queryFactory.selectFrom(largeHoldingsStkrt)
                                                                                                  .where(
                                                                                                          corpCodeEq(condition.getCorpCode())
                                                                                                  )
                                                                                                  .orderBy(
                                                                                                          dynamicOrder(LargeHoldingsStkrt.Fields.rceptNo, true)
                                                                                                  )
                                                                                                  .limit(condition.getLimit())
                                                                                                  .fetchOne());

        if (findOneByRecentRecptNoDesc.isEmpty()) {
            return new ArrayList<>();
        }

        if (!StringUtils.hasText(findOneByRecentRecptNoDesc.get()
                                                           .getRceptNo())) {
            return new ArrayList<>();
        }

        String rceptNo = findOneByRecentRecptNoDesc.get()
                                                   .getRceptNo();

        return queryFactory.selectFrom(largeHoldingsStkrt)
                           .where(
                                   rceptNoEq(rceptNo),
                                   corpCodeEq(condition.getCorpCode())
                           )
                           .orderBy(
                                   dynamicOrder(LargeHoldingsStkrt.Fields.stkrt, true)
                           )
                           .fetch();
    }

    private BooleanExpression corpCodeEq(String corpCode) {
        return corpCode != null ? largeHoldingsStkrt.corpCode.eq(corpCode) : null;
    }

    private BooleanExpression rceptNoEq(String rceptNo) {
        return rceptNo != null ? largeHoldingsStkrt.rceptNo.eq(rceptNo) : null;
    }

    private OrderSpecifier<?> dynamicOrder(String orderColumn, Boolean isDescending) {
        if (!hasText(orderColumn)) {
            return largeHoldingsStkrt.rceptNo.asc();
        }

        if (isDescending == null) {
            isDescending = false;
        }

        Class<?> clazz = QLargeHoldingsStkrt.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(largeHoldingsStkrt.getType(), largeHoldingsStkrt.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return largeHoldingsStkrt.rceptNo.asc();
    }
}
