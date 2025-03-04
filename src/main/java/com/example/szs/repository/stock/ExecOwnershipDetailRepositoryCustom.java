package com.example.szs.repository.stock;

import com.example.szs.domain.stock.ExecOwnershipDetailEntity;
import com.example.szs.domain.stock.QExecOwnershipDetailEntity;
import com.example.szs.model.dto.execOwnership.ExecOwnershipDetailDTO;
import com.example.szs.model.queryDSLSearch.ExecOwnershipDetailSearchCondition;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import com.example.szs.utils.jpa.ListDivider;
import com.example.szs.utils.jpa.Param;
import com.example.szs.utils.time.TimeUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.szs.domain.stock.QExecOwnershipDetailEntity.execOwnershipDetailEntity;
import static org.springframework.util.StringUtils.hasText;

@Repository
@Slf4j
public class ExecOwnershipDetailRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final ExecOwnershipDetailRepository execOwnershipDetailRepository;

    public ExecOwnershipDetailRepositoryCustom(EntityManager em, ExecOwnershipDetailRepository execOwnershipDetailRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.execOwnershipDetailRepository = execOwnershipDetailRepository;
    }

    public Page<ExecOwnershipDetailDTO> searchPage(ExecOwnershipDetailSearchCondition condition, Pageable pageable) {
        List<ExecOwnershipDetailDTO> content = queryFactory.selectFrom(execOwnershipDetailEntity)
                                                           .where(searchPageWhereCondition(condition))
                                                           .orderBy(dynamicOrder(condition))
                                                           .offset(pageable.getOffset())
                                                           .limit(pageable.getPageSize())
                                                           .fetch()
                                                           .stream()
                                                           .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, ExecOwnershipDetailDTO.class)
                                                                                               .stream())
                                                           .toList();

        long totalCount = queryFactory.select(execOwnershipDetailEntity.count())
                                      .from(execOwnershipDetailEntity)
                                      .where(
                                              searchPageWhereCondition(condition)
                                      ).fetch().get(0);

        return new PageImpl<>(content, pageable, totalCount);
    }

    public List<ExecOwnershipDetailDTO> getExecOwnershipDetailDTOList(ExecOwnershipDetailSearchCondition condition) {
        return queryFactory.selectFrom(execOwnershipDetailEntity)
                           .where(
                                   corpCodeEq(condition.getCorpCodeEq()),
                                   execOwnershipNameEq(condition.getExecOwnershipNameEq())
                           )
                           .orderBy(dynamicOrder(condition))
                           .fetch()
                           .stream()
                           .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, ExecOwnershipDetailDTO.class).stream())
                           .collect(Collectors.toList());
    }

    public void saveAll(List<ExecOwnershipDetailDTO> execOwnershipDetailDTOList) {
        if (CollectionUtils.isEmpty(execOwnershipDetailDTOList)) {
            return;
        }

        Map<Boolean, List<ExecOwnershipDetailDTO>> partitioned = execOwnershipDetailDTOList.stream()
                                                                                           .collect(Collectors.partitioningBy(dto -> dto.getSeq() == null || dto.getSeq() == 0L));

        update(partitioned.get(false));
        insert(partitioned.get(true));
    }

    private void insert(List<ExecOwnershipDetailDTO> insertDTOList) {
        if (CollectionUtils.isEmpty(insertDTOList)) {
            return;
        }

        List<ExecOwnershipDetailEntity> insertEntityList = new ArrayList<>();

        for (ExecOwnershipDetailDTO dto : insertDTOList) {
            Optional<ExecOwnershipDetailEntity.ExecOwnershipDetailEntityBuilder> optional = Param.getSaveEntityToBuilder(dto, new ExecOwnershipDetailEntity(), new ExecOwnershipDetailEntity().toBuilder());
            optional.ifPresent(value -> insertEntityList.add(value.build()
                                                                  .toBuilder()
                                                                  .regDt(TimeUtil.nowTime("yyyyMMddHHmmss"))
                                                                  .build()));
        }

        execOwnershipDetailRepository.saveAll(insertEntityList);
    }

    private void update(List<ExecOwnershipDetailDTO> updateDTOList) {
        if (CollectionUtils.isEmpty(updateDTOList)) {
            return;
        }

        List<Long> distinctSeqList = updateDTOList.stream()
                                                  .map(ExecOwnershipDetailDTO::getSeq)
                                                  .collect(Collectors.toSet())
                                                  .stream()
                                                  .collect(Collectors.toList());

        Map<Long, ExecOwnershipDetailEntity> findUpdateEntityMap = new HashMap<>();

        for (List<Long> seqList : ListDivider.getDivisionList(distinctSeqList, 300)) {
            findUpdateEntityMap.putAll(queryFactory.selectFrom(execOwnershipDetailEntity)
                                                   .where(execOwnershipDetailEntity.seq.in(seqList))
                                                   .fetch()
                                                   .stream()
                                                   .collect(Collectors.toMap(ExecOwnershipDetailEntity::getSeq, Function.identity(), (oldValue, newValue) -> newValue)));

        }

        List<ExecOwnershipDetailEntity> updateEntityList = new ArrayList<>();

        for (ExecOwnershipDetailDTO dto : updateDTOList) {
            ExecOwnershipDetailEntity findUpdateEntity = findUpdateEntityMap.getOrDefault(dto.getSeq(), null);

            if (findUpdateEntity == null) {
                continue;
            }

            Optional<ExecOwnershipDetailEntity.ExecOwnershipDetailEntityBuilder> optional = Param.getSaveEntityToBuilder(dto, findUpdateEntity, findUpdateEntity.toBuilder());
            optional.ifPresent(value -> updateEntityList.add(value.build()));
        }

        execOwnershipDetailRepository.saveAll(updateEntityList);
    }

    private BooleanExpression corpCodeEq(String corpCode) {
        return StringUtils.hasText(corpCode) ? execOwnershipDetailEntity.corpCode.eq(corpCode) : null;
    }

    private BooleanExpression execOwnershipNameEq(String execOwnershipName) {
        return StringUtils.hasText(execOwnershipName) ? execOwnershipDetailEntity.execOwnershipName.eq(execOwnershipName) : null;
    }

    private BooleanExpression tradeReasonEq(String tradeReason) {
        return StringUtils.hasText(tradeReason) ? execOwnershipDetailEntity.tradeReason.eq(tradeReason) : null;
    }

    private BooleanExpression stockTypeEq(String stockType) {
        return StringUtils.hasText(stockType) ? execOwnershipDetailEntity.stockType.eq(stockType) : null;
    }

    private BooleanExpression execOwnershipNameContains(String execOwnershipName) {
        return StringUtils.hasText(execOwnershipName) ? execOwnershipDetailEntity.execOwnershipName.contains(execOwnershipName) : null;
    }

    private BooleanExpression tradeReasonContains(String tradeReason) {
        return StringUtils.hasText(tradeReason) ? execOwnershipDetailEntity.tradeReason.contains(tradeReason) : null;
    }

    private BooleanExpression stockTypeContains(String stockType) {
        return StringUtils.hasText(stockType) ? execOwnershipDetailEntity.stockType.contains(stockType) : null;
    }

    private OrderSpecifier<?> dynamicOrder(ExecOwnershipDetailSearchCondition condition) {
        if (!hasText(condition.getOrderColumn())) {
            return execOwnershipDetailEntity.rceptNo.asc();
        }

        String orderColumn = condition.getOrderColumn();
        boolean isDescending = condition.getIsDescending() != null && condition.getIsDescending();

        Class<?> clazz = QExecOwnershipDetailEntity.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(execOwnershipDetailEntity.getType(), execOwnershipDetailEntity.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return execOwnershipDetailEntity.rceptNo.asc();
    }

    private BooleanExpression tradeDtBetween(String tradeDtLoe, String tradeDtGoe) {
        if (!StringUtils.hasText(tradeDtLoe) || !StringUtils.hasText(tradeDtGoe)) {
            return null;
        }

        return tradeDtLoe(tradeDtLoe).and(tradeDtGoe(tradeDtGoe));
    }

    private BooleanExpression tradeDtGoe(String tradeDtGoe) {
        return tradeDtGoe != null ? execOwnershipDetailEntity.tradeDt.goe(tradeDtGoe) : null;
    }

    private BooleanExpression tradeDtLoe(String tradeDtLoe) {
        return tradeDtLoe != null ? execOwnershipDetailEntity.tradeDt.loe(tradeDtLoe) : null;
    }

    // 거래량 changeStockAmount 범위
    private BooleanExpression changeStockAmountGoe(Long changeStockAmountGoe) {
        return changeStockAmountGoe != null ? execOwnershipDetailEntity.changeStockAmount.goe(changeStockAmountGoe) : null;
    }

    private BooleanExpression changeStockAmountGt(Long changeStockAmountGt) {
        return changeStockAmountGt != null ? execOwnershipDetailEntity.changeStockAmount.gt(changeStockAmountGt) : null;
    }

    private BooleanExpression changeStockAmountLoe(Long changeStockAmountLoe) {
        return changeStockAmountLoe != null ? execOwnershipDetailEntity.changeStockAmount.loe(changeStockAmountLoe) : null;
    }

    private BooleanExpression changeStockAmountLt(Long changeStockAmountLt) {
        return changeStockAmountLt != null ? execOwnershipDetailEntity.changeStockAmount.lt(changeStockAmountLt) : null;
    }

    // 평단가 unitStockPrice 범위
    private BooleanExpression unitStockPriceGoe(Long unitStockPriceGoe) {
        return unitStockPriceGoe != null ? execOwnershipDetailEntity.unitStockPrice.goe(unitStockPriceGoe) : null;
    }

    private BooleanExpression unitStockPriceLoe(Long unitStockPriceLoe) {
        return unitStockPriceLoe != null ? execOwnershipDetailEntity.unitStockPrice.loe(unitStockPriceLoe) : null;
    }

    // 보유주식 afterStockAmount 범위
    private BooleanExpression afterStockAmountGoe(Long afterStockAmountGoe) {
        return afterStockAmountGoe != null ? execOwnershipDetailEntity.afterStockAmount.goe(afterStockAmountGoe) : null;
    }

    private BooleanExpression afterStockAmountLoe(Long afterStockAmountLoe) {
        return afterStockAmountLoe != null ? execOwnershipDetailEntity.afterStockAmount.loe(afterStockAmountLoe): null;
    }

    private BooleanBuilder searchPageWhereCondition(ExecOwnershipDetailSearchCondition condition) {
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
        builder.and(unitStockPriceGoe(condition.getUnitStockPriceGoe()));
        builder.and(afterStockAmountGoe(condition.getAfterStockAmountGoe()));

        // Less than or equal (LOE)
        builder.and(changeStockAmountLoe(condition.getChangeStockAmountLoe()));
        builder.and(unitStockPriceLoe(condition.getUnitStockPriceLoe()));
        builder.and(afterStockAmountLoe(condition.getAfterStockAmountLoe()));

        return builder;
    }
}
