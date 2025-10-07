package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.ExecOwnershipDetail;
import com.example.szs.insideTrade.domain.ExecOwnershipDetailRepo;
import com.example.szs.insideTrade.domain.QExecOwnershipDetail;
import com.example.szs.insideTrade.presentation.dto.request.ExecOwnershipDetailSearchConditionReqDTO;
import com.example.szs.insideTrade.application.dto.ExecOwnershipDetailDTO;
import com.example.szs.insideTrade.application.dto.LargeHoldingsDetailDTO;
import com.example.szs.utils.batch.JdbcBatchUtil;
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

import static com.example.szs.insideTrade.domain.QExecOwnershipDetail.execOwnershipDetail;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ExecOwnershipDetailJpaQueryDSLRepo implements ExecOwnershipDetailRepo {
    private final IExecOwnershipDetailJpaRepo iExecOwnershipDetailJpaRepo;
    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate    jdbcTemplate;
    
    private static final String ABS_CODE = "abs({0})";

    @Override
    public void insertNativeBatch(List<ExecOwnershipDetail> entities, int batchSize) {
        String sql = """
                INSERT INTO public.exec_ownership_detail
                (rcept_no, corp_code, corp_name,
                exec_ownership_name, isu_exctv_rgist_at, isu_exctv_ofcps,
                isu_main_shrholdr, trade_dt, trade_reason,
                stock_type, before_stock_amount, change_stock_amount,
                after_stock_amount, unit_stock_price, reg_dt)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        JdbcBatchUtil.executeBatch(jdbcTemplate, entities, batchSize, sql, (ps, entity) -> {
            ps.setString(1, entity.getRceptNo());
            ps.setString(2, entity.getCorpCode());
            ps.setString(3, entity.getCorpName());
            ps.setString(4, entity.getExecOwnershipName());
            ps.setString(5, entity.getIsuExctvRgistAt());
            ps.setString(6, entity.getIsuExctvOfcps());
            ps.setString(7, entity.getIsuMainShrholdr());
            ps.setString(8, entity.getTradeDt());
            ps.setString(9, entity.getTradeReason());
            ps.setString(10, entity.getStockType());
            ps.setObject(11, entity.getBeforeStockAmount());
            ps.setObject(12, entity.getChangeStockAmount());
            ps.setObject(13, entity.getAfterStockAmount());
            ps.setString(14, entity.getUnitStockPrice());
            ps.setString(15, entity.getRegDt());
        });
    }

    @Override
    public Page<ExecOwnershipDetail> searchPage(ExecOwnershipDetailSearchConditionReqDTO condition, Pageable pageable) {
        List<ExecOwnershipDetail> content = queryFactory.selectFrom(execOwnershipDetail)
                                                           .where(searchPageWhereCondition(condition))
                                                           .orderBy(dynamicOrder(condition))
                                                           .offset(pageable.getOffset())
                                                           .limit(pageable.getPageSize())
                                                           .fetch()
                                                           .stream().collect(Collectors.toCollection(()-> new ArrayList<>(pageable.getPageSize())));


        long totalCount = queryFactory.select(execOwnershipDetail.count())
                                      .from(execOwnershipDetail)
                                      .where(
                                              searchPageWhereCondition(condition)
                                      ).fetch().get(0);

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public List<ExecOwnershipDetail> getExecOwnershipDetailList(ExecOwnershipDetailSearchConditionReqDTO condition) {
        return queryFactory.selectFrom(execOwnershipDetail)
                           .where(
                                   corpCodeEq(condition.getCorpCodeEq()),
                                   execOwnershipNameEq(condition.getExecOwnershipNameEq())
                           )
                           .orderBy(dynamicOrder(condition))
                           .fetch()
                           .stream()
                           .collect(Collectors.toCollection(() -> new ArrayList<>(10)));
    }

    @Override
    public List<ExecOwnershipDetailDTO.MonthlyCountDTO> getMonthlyTradeCnt(String corpCode, boolean isSell) {
        if (corpCode == null) {
            return new ArrayList<>();
        }

        StringExpression subStringTradeDt = execOwnershipDetail.tradeDt.stringValue().substring(0, 6);

        return queryFactory.select(Projections.constructor(ExecOwnershipDetailDTO.MonthlyCountDTO.class,
                                   subStringTradeDt.as(ExecOwnershipDetailDTO.MonthlyCountDTO.Fields.month),
                                   execOwnershipDetail.corpCode.count().as(ExecOwnershipDetailDTO.MonthlyCountDTO.Fields.count)
                           )).
                           from(execOwnershipDetail)
                           .where(
                                   corpCodeEq(corpCode),
                                   changeStockAmountLt(isSell ? 0L : null), // 매도
                                   changeStockAmountGt(!isSell ? 0L : null), // 매수
                                   execOwnershipDetail.tradeDt.isNotNull(),
                                   execOwnershipDetail.tradeDt.isNotEmpty(),
                                   execOwnershipDetail.tradeDt.ne("-")
                           )
                           .groupBy(subStringTradeDt)
                           .orderBy(subStringTradeDt.asc())
                           .fetch();
    }

    @Override
    public List<ExecOwnershipDetailDTO.TopStockDetailDTO> getTopStockDetail(ExecOwnershipDetailSearchConditionReqDTO condition) {
        JPAQuery<ExecOwnershipDetailDTO.TopStockDetailDTO> query = queryFactory.select(Projections.constructor(ExecOwnershipDetailDTO.TopStockDetailDTO.class,
                                                                                       execOwnershipDetail.corpCode.as(LargeHoldingsDetailDTO.TopStockDetailDTO.Fields.corpCode),
                                                                                       execOwnershipDetail.corpName.as(LargeHoldingsDetailDTO.TopStockDetailDTO.Fields.corpName),
                                                                                       Expressions.numberTemplate(Long.class, ABS_CODE, execOwnershipDetail.changeStockAmount.sum()).as(ExecOwnershipDetailDTO.TopStockDetailDTO.Fields.totalStockAmount)))
                                                                               .from(execOwnershipDetail)
                                                                               .where(
                                                                                       changeStockAmountLt(condition.getChangeStockAmountLt()),
                                                                                       changeStockAmountGt(condition.getChangeStockAmountGt()),
                                                                                       tradeDtBetween(condition.getTradeDtLoe(), condition.getTradeDtGoe())
                                                                               )
                                                                               .groupBy(execOwnershipDetail.corpCode, execOwnershipDetail.corpName)
                                                                               .orderBy(Expressions.numberTemplate(Long.class, ABS_CODE, execOwnershipDetail.changeStockAmount).sum().desc());

        if (condition.getLimit() != null && condition.getLimit() != 0) {
            query.limit(condition.getLimit());
        }
        return query.fetch();
    }

    private BooleanExpression corpCodeEq(String corpCode) {
        return StringUtils.hasText(corpCode) ? execOwnershipDetail.corpCode.eq(corpCode) : null;
    }

    private BooleanExpression execOwnershipNameEq(String execOwnershipName) {
        return StringUtils.hasText(execOwnershipName) ? execOwnershipDetail.execOwnershipName.eq(execOwnershipName) : null;
    }

    private BooleanExpression tradeReasonEq(String tradeReason) {
        return StringUtils.hasText(tradeReason) ? execOwnershipDetail.tradeReason.eq(tradeReason) : null;
    }

    private BooleanExpression stockTypeEq(String stockType) {
        return StringUtils.hasText(stockType) ? execOwnershipDetail.stockType.eq(stockType) : null;
    }

    private BooleanExpression execOwnershipNameContains(String execOwnershipName) {
        return StringUtils.hasText(execOwnershipName) ? execOwnershipDetail.execOwnershipName.contains(execOwnershipName) : null;
    }

    private BooleanExpression tradeReasonContains(String tradeReason) {
        return StringUtils.hasText(tradeReason) ? execOwnershipDetail.tradeReason.contains(tradeReason) : null;
    }

    private BooleanExpression stockTypeContains(String stockType) {
        return StringUtils.hasText(stockType) ? execOwnershipDetail.stockType.contains(stockType) : null;
    }

    private OrderSpecifier<?> dynamicOrder(ExecOwnershipDetailSearchConditionReqDTO condition) {
        if (!hasText(condition.getOrderColumn())) {
            return execOwnershipDetail.rceptNo.asc();
        }

        String orderColumn = condition.getOrderColumn();
        boolean isDescending = condition.getIsDescending() != null && condition.getIsDescending();

        Class<?> clazz = QExecOwnershipDetail.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(execOwnershipDetail.getType(), execOwnershipDetail.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return execOwnershipDetail.rceptNo.asc();
    }

    private BooleanExpression tradeDtBetween(String tradeDtLoe, String tradeDtGoe) {
        if (!StringUtils.hasText(tradeDtLoe) || !StringUtils.hasText(tradeDtGoe)) {
            return null;
        }

        return tradeDtLoe(tradeDtLoe).and(tradeDtGoe(tradeDtGoe));
    }

    private BooleanExpression tradeDtGoe(String tradeDtGoe) {
        return tradeDtGoe != null ? execOwnershipDetail.tradeDt.goe(tradeDtGoe) : null;
    }

    private BooleanExpression tradeDtLoe(String tradeDtLoe) {
        return tradeDtLoe != null ? execOwnershipDetail.tradeDt.loe(tradeDtLoe) : null;
    }

    // 거래량 changeStockAmount 범위
    private BooleanExpression changeStockAmountGoe(Long changeStockAmountGoe) {
        return changeStockAmountGoe != null ? execOwnershipDetail.changeStockAmount.goe(changeStockAmountGoe) : null;
    }

    private BooleanExpression changeStockAmountGt(Long changeStockAmountGt) {
        return changeStockAmountGt != null ? execOwnershipDetail.changeStockAmount.gt(changeStockAmountGt) : null;
    }

    private BooleanExpression changeStockAmountLoe(Long changeStockAmountLoe) {
        return changeStockAmountLoe != null ? execOwnershipDetail.changeStockAmount.loe(changeStockAmountLoe) : null;
    }

    private BooleanExpression changeStockAmountLt(Long changeStockAmountLt) {
        return changeStockAmountLt != null ? execOwnershipDetail.changeStockAmount.lt(changeStockAmountLt) : null;
    }

    // 보유주식 afterStockAmount 범위
    private BooleanExpression afterStockAmountGoe(Long afterStockAmountGoe) {
        return afterStockAmountGoe != null ? execOwnershipDetail.afterStockAmount.goe(afterStockAmountGoe) : null;
    }

    private BooleanExpression afterStockAmountLoe(Long afterStockAmountLoe) {
        return afterStockAmountLoe != null ? execOwnershipDetail.afterStockAmount.loe(afterStockAmountLoe): null;
    }

    private BooleanBuilder searchPageWhereCondition(ExecOwnershipDetailSearchConditionReqDTO condition) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(execOwnershipNameEq(condition.getExecOwnershipNameEq()));
        builder.and(corpCodeEq(condition.getCorpCodeEq()));
        builder.and(tradeReasonEq(condition.getTradeReasonEq()));
        builder.and(stockTypeEq(condition.getStockTypeEq()));

        // Between
        builder.and(tradeDtBetween(condition.getTradeDtLoe(), condition.getTradeDtGoe()));

        // Like
        builder.and(execOwnershipNameContains(condition.getExecOwnershipNameContains()));
        builder.and(tradeReasonContains(condition.getTradeReasonContains()));
        builder.and(stockTypeContains(condition.getStockTypeContains()));

        // Greater than or equal (GOE)
        builder.and(changeStockAmountGoe(condition.getChangeStockAmountGoe()));
        builder.and(afterStockAmountGoe(condition.getAfterStockAmountGoe()));

        // Less than or equal (LOE)
        builder.and(changeStockAmountLoe(condition.getChangeStockAmountLoe()));
        builder.and(afterStockAmountLoe(condition.getAfterStockAmountLoe()));

        return builder;
    }
}
