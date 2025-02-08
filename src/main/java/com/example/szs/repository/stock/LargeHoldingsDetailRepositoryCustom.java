package com.example.szs.repository.stock;

import com.example.szs.domain.stock.LargeHoldingsDetailEntity;
import com.example.szs.domain.stock.QLargeHoldingsDetailEntity;
import com.example.szs.model.dto.LargeHoldingsDetailDTO;
import com.example.szs.model.queryDSLSearch.LargeHoldingsDetailSearchCondition;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import com.example.szs.utils.jpa.Param;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.szs.domain.stock.QLargeHoldingsDetailEntity.largeHoldingsDetailEntity;
import static org.springframework.util.StringUtils.hasText;

@Repository
@Slf4j
public class LargeHoldingsDetailRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final LargeHoldingsDetailRepository largeHoldingsDetailRepository;

    public LargeHoldingsDetailRepositoryCustom(EntityManager em, LargeHoldingsDetailRepository largeHoldingsDetailRepository) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.largeHoldingsDetailRepository = largeHoldingsDetailRepository;
    }

    public Page<LargeHoldingsDetailDTO> searchPage(LargeHoldingsDetailSearchCondition condtion, Pageable pageable) {
        List<LargeHoldingsDetailDTO> content = queryFactory.selectFrom(largeHoldingsDetailEntity)
                                                           .where(
                                                                   // eq
                                                                   largeHoldingsNameEq      (condtion.getLargeHoldingsNameEq()),
                                                                   birthDateOrBizRegNumEq   (condtion.getBirthDateOrBizRegNumEq()),
                                                                   tradeReasonEq            (condtion.getTradeReasonEq()),
                                                                   stockTypeEq              (condtion.getStockTypeEq()),
                                                                   tradeDtBetween           (condtion.getTradeDtLoe(), condtion.getTradeDtGoe()),
                                                                   // like
                                                                   largeHoldingsNameContains    (condtion.getLargeHoldingsNameContains()),
                                                                   birthDateOrBizRegNumContains (condtion.getBirthDateOrBizRegNumEqContains()),
                                                                   tradeReasonContains          (condtion.getTradeReasonContains()),
                                                                   stockTypeContains            (condtion.getStockTypeContains())
                                                           )
                                                           .orderBy(dynamicOrder(condtion))
                                                           .offset(pageable.getOffset())
                                                           .limit(pageable.getPageSize())
                                                           .fetch()
                                                           .stream()
                                                           .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDetailDTO.class).stream())
                                                           .toList();

        JPAQuery<Long> countQuery = queryFactory.select(largeHoldingsDetailEntity.count())
                                                .from(largeHoldingsDetailEntity)
                                                .where(
                                                        largeHoldingsNameEq     (condtion.getLargeHoldingsNameEq()),
                                                        birthDateOrBizRegNumEq  (condtion.getBirthDateOrBizRegNumEq()),
                                                        tradeReasonEq           (condtion.getTradeReasonEq()),
                                                        stockTypeEq             (condtion.getStockTypeEq()),
                                                        tradeDtBetween          (condtion.getTradeDtLoe(), condtion.getTradeDtGoe())
                                                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    public void saveLargeHoldingsDetail(List<LargeHoldingsDetailDTO> largeHoldingsDetailDTOList) {
        if (CollectionUtils.isEmpty(largeHoldingsDetailDTOList)) {
            return;
        }
        Map<Boolean, List<LargeHoldingsDetailDTO>> partitioned = largeHoldingsDetailDTOList.stream()
                                                                                           .collect(Collectors.partitioningBy(dto -> dto.getSeq() == null || dto.getSeq() == 0L));

        // ############# update ############# [start]
        List<LargeHoldingsDetailDTO> updateDTOList = partitioned.get(false);

        if (!CollectionUtils.isEmpty(updateDTOList)) {
            List<Long> seqList = updateDTOList.stream().map(LargeHoldingsDetailDTO::getSeq).toList();

            Map<Long, LargeHoldingsDetailEntity> findUpdateEntityMap = queryFactory.selectFrom(largeHoldingsDetailEntity)
                                                                                   .where(largeHoldingsDetailEntity.seq.in(seqList))
                                                                                   .fetch()
                                                                                   .stream()
                                                                                   .collect(Collectors.toMap(LargeHoldingsDetailEntity::getSeq, Function.identity(), (oldValue, newValue) -> newValue));

            List<LargeHoldingsDetailEntity> updateEntityList = new ArrayList<>();

            for (LargeHoldingsDetailDTO dto : updateDTOList) {
                LargeHoldingsDetailEntity findUpdateEntity = findUpdateEntityMap.getOrDefault(dto.getSeq(), null);

                if (findUpdateEntity == null) {
                    continue;
                }

                Optional<LargeHoldingsDetailEntity.LargeHoldingsDetailEntityBuilder> optaional = Param.getSaveEntityToBuilder(dto, findUpdateEntity, findUpdateEntity.toBuilder());
                optaional.ifPresent(value -> updateEntityList.add(value.build()));
            }

            largeHoldingsDetailRepository.saveAll(updateEntityList);
        }
        // ############# update ############# [end]

        // ############# insert ############# [start]
        List<LargeHoldingsDetailDTO> insertDTOList = partitioned.get(true);
        List<LargeHoldingsDetailEntity> insertEntityList = new ArrayList<>();

        for (LargeHoldingsDetailDTO dto : insertDTOList) {
            Optional<LargeHoldingsDetailEntity.LargeHoldingsDetailEntityBuilder> optaional = Param.getSaveEntityToBuilder(dto, new LargeHoldingsDetailEntity(), new LargeHoldingsDetailEntity().toBuilder());
            optaional.ifPresent(value -> insertEntityList.add(value.build()));
        }

        largeHoldingsDetailRepository.saveAll(insertEntityList);
        // ############# insert ############# [start]
    }

    private BooleanExpression largeHoldingsNameEq(String largeHoldingsName) {
        return StringUtils.hasText(largeHoldingsName) ? largeHoldingsDetailEntity.largeHoldingsName.eq(largeHoldingsName) : null;
    }

    private BooleanExpression birthDateOrBizRegNumEq(String birthDateOrBizRegNum) {
        return StringUtils.hasText(birthDateOrBizRegNum) ? largeHoldingsDetailEntity.birthDateOrBizRegNum.eq(birthDateOrBizRegNum) : null;
    }

    private BooleanExpression tradeReasonEq(String tradeReason) {
        return StringUtils.hasText(tradeReason) ? largeHoldingsDetailEntity.tradeReason.eq(tradeReason) : null;
    }

    private BooleanExpression stockTypeEq(String stockType) {
        return StringUtils.hasText(stockType) ? largeHoldingsDetailEntity.stockType.eq(stockType) : null;
    }

    private BooleanExpression largeHoldingsNameContains(String largeHoldingsName) {
        return StringUtils.hasText(largeHoldingsName) ? largeHoldingsDetailEntity.largeHoldingsName.contains(largeHoldingsName) : null;
    }

    private BooleanExpression birthDateOrBizRegNumContains(String birthDateOrBizRegNum) {
        return StringUtils.hasText(birthDateOrBizRegNum) ? largeHoldingsDetailEntity.birthDateOrBizRegNum.contains(birthDateOrBizRegNum) : null;
    }

    private BooleanExpression tradeReasonContains(String tradeReason) {
        return StringUtils.hasText(tradeReason) ? largeHoldingsDetailEntity.tradeReason.contains(tradeReason) : null;
    }

    private BooleanExpression stockTypeContains(String stockType) {
        return StringUtils.hasText(stockType) ? largeHoldingsDetailEntity.stockType.contains(stockType) : null;
    }

    private OrderSpecifier<?> dynamicOrder(LargeHoldingsDetailSearchCondition condition) {
        if (!hasText(condition.getOrderColumn())) {
            return largeHoldingsDetailEntity.rceptNo.asc();
        }

        String orderColumn = condition.getOrderColumn();
        boolean isDescending = condition.getIsDescending() != null && condition.getIsDescending();

        Class<?> clazz = QLargeHoldingsDetailEntity.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(largeHoldingsDetailEntity.getType(), largeHoldingsDetailEntity.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return largeHoldingsDetailEntity.rceptNo.asc();
    }

    private BooleanExpression tradeDtBetween(String tradeDtLoe, String tradeDtGoe) {
        if (!StringUtils.hasText(tradeDtLoe) || !StringUtils.hasText(tradeDtGoe)) {
            return null;
        }

        return tradeDtLoe(tradeDtLoe).and(tradeDtGoe(tradeDtGoe));
    }

    private BooleanExpression tradeDtGoe(String tradeDtGoe) {
        return tradeDtGoe != null ? largeHoldingsDetailEntity.tradeDt.goe(tradeDtGoe) : null;
    }

    private BooleanExpression tradeDtLoe(String tradeDtLoe) {
        return tradeDtLoe != null ? largeHoldingsDetailEntity.tradeDt.loe(tradeDtLoe) : null;
    }
}
