package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.LargeHoldingsDetail;
import com.example.szs.insideTrade.domain.LargeHoldingsDetailRepo;
import com.example.szs.insideTrade.domain.QLargeHoldingsDetail;
import com.example.szs.insideTrade.presentation.dto.request.LargeHoldingsDetailSearchConditionReqDTO;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsDetailDTO;
import com.example.szs.utils.batch.JdbcBatchUtil;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.szs.insideTrade.domain.QLargeHoldingsDetail.largeHoldingsDetail;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsDetailJpaOrQueryDSLRepo implements LargeHoldingsDetailRepo {
    private final ILargeHoldingsDetailJpaRepo iLargeHoldingsDetailJpaRepo;
    private final JdbcTemplate                jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    private static final String ABS_CODE = "abs({0})";

    @Override
    public List<LargeHoldingsDetail> saveAll(List<LargeHoldingsDetail> entities) {
        return iLargeHoldingsDetailJpaRepo.saveAll(entities);
    }

    @Override
    public void insertNativeBatch(List<LargeHoldingsDetail> entities, int batchSize) {
        String sql = """
            INSERT INTO large_holdings_detail
                (rcept_no, corp_code, corp_name, large_holdings_name, birth_date_or_biz_reg_num,
                 trade_dt, trade_reason, stock_type, before_stock_amount, change_stock_amount,
                 after_stock_amount, unit_stock_price, currency_type, total_stock_price, reg_dt)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        JdbcBatchUtil.executeBatch(jdbcTemplate, entities, batchSize, sql, (ps, entity) -> {
            ps.setString(1, entity.getRceptNo());
            ps.setString(2, entity.getCorpCode());
            ps.setString(3, entity.getCorpName());
            ps.setString(4, entity.getLargeHoldingsName());
            ps.setString(5, entity.getBirthDateOrBizRegNum());
            ps.setString(6, entity.getTradeDt());
            ps.setString(7, entity.getTradeReason());
            ps.setString(8, entity.getStockType());
            ps.setObject(9, entity.getBeforeStockAmount());
            ps.setObject(10, entity.getChangeStockAmount());
            ps.setObject(11, entity.getAfterStockAmount());
            ps.setObject(12, entity.getUnitStockPrice());
            ps.setString(13, entity.getCurrencyType());
            ps.setObject(14, entity.getTotalStockPrice());
            ps.setString(15, entity.getRegDt());
        });
    }

    @Override
    public Page<LargeHoldingsDetail> searchPage(LargeHoldingsDetailSearchConditionReqDTO condition, Pageable pageable) {
        List<LargeHoldingsDetail> content = queryFactory.selectFrom(largeHoldingsDetail)
                                                        .where(
                                                                searchPageWhereCondition(condition)
                                                        )
                                                        .orderBy(dynamicOrder(condition))
                                                        .offset(pageable.getOffset())
                                                        .limit(pageable.getPageSize())
                                                        .fetch();

        long totalCount = queryFactory.select(largeHoldingsDetail.count())
                                      .from(largeHoldingsDetail)
                                      .where(
                                              searchPageWhereCondition(condition)
                                      )
                                      .fetch()
                                      .get(0);

        return new PageImpl<>(content, pageable, totalCount);
    }

//    public Page<LargeHoldingsDetailDTO> searchPage(LargeHoldingsDetailSearchConditionReqDTO condition, Pageable pageable) {
//        List<LargeHoldingsDetailDTO> content = queryFactory.selectFrom(largeHoldingsDetail)
//                                                           .where(
//                                                                   searchPageWhereCondition(condition)
//                                                           )
//                                                           .orderBy(dynamicOrder(condition))
//                                                           .offset(pageable.getOffset())
//                                                           .limit(pageable.getPageSize())
//                                                           .fetch()
//                                                           .stream()
//                                                           .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDetailDTO.class).stream())
//                                                           .toList();
//
//        long totalCount = queryFactory.select(largeHoldingsDetail.count())
//                                      .from(largeHoldingsDetail)
//                                      .where(
//                                              searchPageWhereCondition(condition)
//                                      ).fetch().get(0);
//
//        return new PageImpl<>(content, pageable, totalCount);
//    }
//
//    public List<LargeHoldingsDetailDTO.MonthlyCountDTO> getLargeHoldingsMonthlyTradeCnt(String corpCode, boolean isSell) {
//        if (corpCode == null) {
//            return new ArrayList<>();
//        }
//
//        StringExpression subStringTradeDt = largeHoldingsDetail.tradeDt.stringValue().substring(0, 6);
//
//        return queryFactory.select(Projections.constructor(LargeHoldingsDetailDTO.MonthlyCountDTO.class,
//                                   subStringTradeDt.as(LargeHoldingsDetailDTO.MonthlyCountDTO.Fields.month),
//                                   largeHoldingsDetail.corpCode.count().as(LargeHoldingsDetailDTO.MonthlyCountDTO.Fields.count)
//                           )).
//                           from(largeHoldingsDetail)
//                           .where(
//                                   corpCodeEq(corpCode),
//                                   changeStockAmountLt(isSell ? 0L : null), // 매도
//                                   changeStockAmountGt(!isSell ? 0L : null), // 매수
//                                   largeHoldingsDetail.tradeDt.isNotNull(),
//                                   largeHoldingsDetail.tradeDt.isNotEmpty(),
//                                   largeHoldingsDetail.tradeDt.ne("-")
//                           )
//                           .groupBy(subStringTradeDt)
//                           .orderBy(subStringTradeDt.asc())
//                           .fetch();
//    }
//
//    public List<LargeHoldingsDetailDTO> getLargeHoldingsDetailDTOListBy(LargeHoldingsDetailSearchConditionReqDTO condition) {
//        return queryFactory.selectFrom(largeHoldingsDetail)
//                           .where(
//                                   largeHoldingsNameEq(condition.getLargeHoldingsNameEq()),
//                                   birthDateOrBizRegNumEq(condition.getBirthDateOrBizRegNumEq()),
//                                   tradeReasonEq(condition.getTradeReasonEq()),
//                                   stockTypeEq(condition.getStockTypeEq()),
//                                   corpCodeEq(condition.getCorpCodeEq()),
//
//                                   afterStockAmountGoe(condition.getAfterStockAmountGoe()),
//                                   afterStockAmountLoe(condition.getAfterStockAmountLoe()),
//                                   unitStockPriceGoe(condition.getUnitStockPriceGoe()),
//                                   unitStockPriceLoe(condition.getUnitStockPriceLoe()),
//                                   changeStockAmountGoe(condition.getChangeStockAmountGoe()),
//                                   changeStockAmountLoe(condition.getChangeStockAmountLoe()),
//                                   tradeDtGoe(condition.getTradeDtGoe()),
//                                   tradeDtLoe(condition.getTradeDtLoe()),
//
//                                   largeHoldingsNameContains(condition.getLargeHoldingsNameContains()),
//                                   birthDateOrBizRegNumContains(condition.getBirthDateOrBizRegNumEqContains()),
//                                   tradeReasonContains(condition.getTradeReasonContains()),
//                                   stockTypeContains(condition.getStockTypeContains())
//                           )
//                           .orderBy(dynamicOrder(condition))
//                           .fetch()
//                           .stream()
//                           .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDetailDTO.class).stream())
//                           .toList();
//
//
//    }
//
//    public List<LargeHoldingsDetailDTO.TopStockDetailDTO> getTopStockDetail(LargeHoldingsDetailSearchConditionReqDTO condition) {
//        JPAQuery<LargeHoldingsDetailDTO.TopStockDetailDTO> query = queryFactory.select(Projections.constructor(LargeHoldingsDetailDTO.TopStockDetailDTO.class,
//                                                                                       largeHoldingsDetail.corpCode.as(LargeHoldingsDetailDTO.TopStockDetailDTO.Fields.corpCode),
//                                                                                       largeHoldingsDetail.corpName.as(LargeHoldingsDetailDTO.TopStockDetailDTO.Fields.corpName),
//                                                                                       Expressions.numberTemplate(Long.class, ABS_CODE, largeHoldingsDetail.changeStockAmount.sum()).as(LargeHoldingsDetailDTO.TopStockDetailDTO.Fields.totalStockAmount)
//                                                                               ))
//                                                                               .from(largeHoldingsDetail)
//                                                                               .where(
//                                                                                       changeStockAmountLt(condition.getChangeStockAmountLt()),
//                                                                                       changeStockAmountGt(condition.getChangeStockAmountGt()),
//                                                                                       tradeDtBetween(condition.getTradeDtLoe(), condition.getTradeDtGoe())
//                                                                               )
//                                                                               .groupBy(largeHoldingsDetail.corpCode, largeHoldingsDetail.corpName)
//                                                                               .orderBy(Expressions.numberTemplate(Long.class, ABS_CODE, largeHoldingsDetail.changeStockAmount).sum().desc());
//
//
//        if (condition.getLimit() != null && condition.getLimit() != 0) {
//            query.limit(condition.getLimit());
//        }
//        return query.fetch();
//    }

    private BooleanExpression corpCodeEq(String corpCode) {
        return StringUtils.hasText(corpCode) ? largeHoldingsDetail.corpCode.eq(corpCode) : null;
    }

    private BooleanExpression largeHoldingsNameEq(String largeHoldingsName) {
        return StringUtils.hasText(largeHoldingsName) ? largeHoldingsDetail.largeHoldingsName.eq(largeHoldingsName) : null;
    }

    private BooleanExpression birthDateOrBizRegNumEq(String birthDateOrBizRegNum) {
        return StringUtils.hasText(birthDateOrBizRegNum) ? largeHoldingsDetail.birthDateOrBizRegNum.eq(birthDateOrBizRegNum) : null;
    }

    private BooleanExpression tradeReasonEq(String tradeReason) {
        return StringUtils.hasText(tradeReason) ? largeHoldingsDetail.tradeReason.eq(tradeReason) : null;
    }

    private BooleanExpression stockTypeEq(String stockType) {
        return StringUtils.hasText(stockType) ? largeHoldingsDetail.stockType.eq(stockType) : null;
    }

    private BooleanExpression largeHoldingsNameContains(String largeHoldingsName) {
        return StringUtils.hasText(largeHoldingsName) ? largeHoldingsDetail.largeHoldingsName.contains(largeHoldingsName) : null;
    }

    private BooleanExpression birthDateOrBizRegNumContains(String birthDateOrBizRegNum) {
        return StringUtils.hasText(birthDateOrBizRegNum) ? largeHoldingsDetail.birthDateOrBizRegNum.contains(birthDateOrBizRegNum) : null;
    }

    private BooleanExpression tradeReasonContains(String tradeReason) {
        return StringUtils.hasText(tradeReason) ? largeHoldingsDetail.tradeReason.contains(tradeReason) : null;
    }

    private BooleanExpression stockTypeContains(String stockType) {
        return StringUtils.hasText(stockType) ? largeHoldingsDetail.stockType.contains(stockType) : null;
    }

    private OrderSpecifier<?> dynamicOrder(LargeHoldingsDetailSearchConditionReqDTO condition) {
        if (!hasText(condition.getOrderColumn())) {
            return largeHoldingsDetail.rceptNo.asc();
        }

        String orderColumn = condition.getOrderColumn();
        boolean isDescending = condition.getIsDescending() != null && condition.getIsDescending();

        Class<?> clazz = QLargeHoldingsDetail.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(largeHoldingsDetail.getType(), largeHoldingsDetail.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return largeHoldingsDetail.rceptNo.asc();
    }

    private BooleanExpression tradeDtBetween(String tradeDtLoe, String tradeDtGoe) {
        if (!StringUtils.hasText(tradeDtLoe) || !StringUtils.hasText(tradeDtGoe)) {
            return null;
        }

        return tradeDtLoe(tradeDtLoe).and(tradeDtGoe(tradeDtGoe));
    }

    private BooleanExpression tradeDtGoe(String tradeDtGoe) {
        return tradeDtGoe != null ? largeHoldingsDetail.tradeDt.goe(tradeDtGoe) : null;
    }

    private BooleanExpression tradeDtLoe(String tradeDtLoe) {
        return tradeDtLoe != null ? largeHoldingsDetail.tradeDt.loe(tradeDtLoe) : null;
    }

    // 거래량 changeStockAmount 범위
    private BooleanExpression changeStockAmountGoe(Long changeStockAmountGoe) {
        return changeStockAmountGoe != null ? largeHoldingsDetail.changeStockAmount.goe(changeStockAmountGoe) : null;
    }

    private BooleanExpression changeStockAmountGt(Long changeStockAmountGt) {
        return changeStockAmountGt != null ? largeHoldingsDetail.changeStockAmount.gt(changeStockAmountGt) : null;
    }

    private BooleanExpression changeStockAmountLoe(Long changeStockAmountLoe) {
        return changeStockAmountLoe != null ? largeHoldingsDetail.changeStockAmount.loe(changeStockAmountLoe) : null;
    }

    private BooleanExpression changeStockAmountLt(Long changeStockAmountLt) {
        return changeStockAmountLt != null ? largeHoldingsDetail.changeStockAmount.lt(changeStockAmountLt) : null;
    }

    // 평단가 unitStockPrice 범위
    private BooleanExpression unitStockPriceGoe(Long unitStockPriceGoe) {
        return unitStockPriceGoe != null ? largeHoldingsDetail.unitStockPrice.goe(unitStockPriceGoe) : null;
    }

    private BooleanExpression unitStockPriceLoe(Long unitStockPriceLoe) {
        return unitStockPriceLoe != null ? largeHoldingsDetail.unitStockPrice.loe(unitStockPriceLoe) : null;
    }

    // 보유주식 afterStockAmount 범위
    private BooleanExpression afterStockAmountGoe(Long afterStockAmountGoe) {
        return afterStockAmountGoe != null ? largeHoldingsDetail.afterStockAmount.goe(afterStockAmountGoe) : null;
    }

    private BooleanExpression afterStockAmountLoe(Long afterStockAmountLoe) {
        return afterStockAmountLoe != null ? largeHoldingsDetail.afterStockAmount.loe(afterStockAmountLoe): null;
    }

    private BooleanBuilder searchPageWhereCondition(LargeHoldingsDetailSearchConditionReqDTO condition) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(largeHoldingsNameEq(condition.getLargeHoldingsNameEq()));
        builder.and(corpCodeEq(condition.getCorpCodeEq()));
        builder.and(birthDateOrBizRegNumEq(condition.getBirthDateOrBizRegNumEq()));
        builder.and(tradeReasonEq(condition.getTradeReasonEq()));
        builder.and(stockTypeEq(condition.getStockTypeEq()));

        // Between
        builder.and(tradeDtBetween(condition.getTradeDtLoe(), condition.getTradeDtGoe()));

        // Like
        builder.and(largeHoldingsNameContains(condition.getLargeHoldingsNameContains()));
        builder.and(birthDateOrBizRegNumContains(condition.getBirthDateOrBizRegNumEqContains()));
        builder.and(tradeReasonContains(condition.getTradeReasonContains()));
        builder.and(stockTypeContains(condition.getStockTypeContains()));

        // Greater than or equal (GOE)
        builder.and(changeStockAmountGoe(condition.getChangeStockAmountGoe()));
        builder.and(unitStockPriceGoe(condition.getUnitStockPriceGoe()));
        builder.and(afterStockAmountGoe(condition.getAfterStockAmountGoe()));

        // Less than or equal (LOE)
        builder.and(changeStockAmountLoe(condition.getChangeStockAmountLoe()));
        builder.and(unitStockPriceLoe(condition.getUnitStockPriceLoe()));
        builder.and(afterStockAmountLoe(condition.getAfterStockAmountLoe()));

        return builder;
    }
}
