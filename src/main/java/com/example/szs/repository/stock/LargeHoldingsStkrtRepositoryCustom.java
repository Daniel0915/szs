package com.example.szs.repository.stock;

import com.example.szs.domain.stock.LargeHoldingsStkrtEntity;
import com.example.szs.domain.stock.QLargeHoldingsStkrtEntity;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsStkrtDTO;
import com.example.szs.model.queryDSLSearch.LargeHoldingStkrtSearchCondition;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import com.example.szs.utils.jpa.ListDivider;
import com.example.szs.utils.jpa.Param;
import com.example.szs.utils.time.TimeUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.szs.domain.stock.QLargeHoldingsStkrtEntity.largeHoldingsStkrtEntity;
import static org.springframework.util.StringUtils.hasText;

@Repository
@Slf4j
public class LargeHoldingsStkrtRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final LargeHoldingsStkrtRepository largeHoldingsStkrtRepository;

    public LargeHoldingsStkrtRepositoryCustom(EntityManager em, LargeHoldingsStkrtRepository largeHoldingsStkrtRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.largeHoldingsStkrtRepository = largeHoldingsStkrtRepository;
    }

    public List<LargeHoldingsStkrtDTO> getLargeHoldingsStockRatio(LargeHoldingStkrtSearchCondition condition) {
        if (condition.getCorpCode() == null) {
            return new ArrayList<>();
        }

        // 가장 최근 등록일
        Optional<LargeHoldingsStkrtEntity> findOneByRecentRecptNoDesc = Optional.ofNullable(queryFactory.selectFrom(largeHoldingsStkrtEntity)
                                                                                                        .where(
                                                                                                                corpCodeEq(condition.getCorpCode())
                                                                                                        )
                                                                                                        .orderBy(
                                                                                                                dynamicOrder(LargeHoldingsStkrtEntity.Fields.rceptNo, true)
                                                                                                        )
                                                                                                        .limit(condition.getLimit())
                                                                                                        .fetchOne());

        if (findOneByRecentRecptNoDesc.isEmpty()) {
            return new ArrayList<>();
        }

        if (!StringUtils.hasText(findOneByRecentRecptNoDesc.get().getRceptNo())) {
            return new ArrayList<>();
        }

        String rceptNo = findOneByRecentRecptNoDesc.get().getRceptNo();

        return queryFactory.selectFrom(largeHoldingsStkrtEntity)
                           .where(
                                   rceptNoEq(rceptNo),
                                   corpCodeEq(condition.getCorpCode())
                           )
                           .orderBy(
                                   dynamicOrder(LargeHoldingsStkrtEntity.Fields.stkrt, true)
                           )
                           .stream()
                           .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsStkrtDTO.class).stream())
                           .toList();
    }

    public void saveAll(List<LargeHoldingsStkrtDTO> largeHoldingsStkrtDTOList) {
        if (CollectionUtils.isEmpty(largeHoldingsStkrtDTOList)) {
            return;
        }
        Map<Boolean, List<LargeHoldingsStkrtDTO>> partitioned = largeHoldingsStkrtDTOList.stream()
                                                                                           .collect(Collectors.partitioningBy(dto -> dto.getSeq() == null || dto.getSeq() == 0L));

        update(partitioned.get(false));
        insert(partitioned.get(true));
    }

    private void insert(List<LargeHoldingsStkrtDTO> insertDTOList) {
        if (CollectionUtils.isEmpty(insertDTOList)) {
            return;
        }
        List<LargeHoldingsStkrtEntity> insertEntityList = new ArrayList<>();

        for (LargeHoldingsStkrtDTO dto : insertDTOList) {
            Optional<LargeHoldingsStkrtEntity.LargeHoldingsStkrtEntityBuilder> optional = Param.getSaveEntityToBuilder(dto, new LargeHoldingsStkrtEntity(), new LargeHoldingsStkrtEntity().toBuilder());
            optional.ifPresent(value -> insertEntityList.add(value.build()
                                                                  .toBuilder()
                                                                  .regDt(TimeUtil.nowTime("yyyyMMddHHmmss"))
                                                                  .build()));
        }

        largeHoldingsStkrtRepository.saveAll(insertEntityList);
    }

    private void update(List<LargeHoldingsStkrtDTO> updateDTOList) {
        if (CollectionUtils.isEmpty(updateDTOList)) {
            return;
        }
        List<Long> distinctSeqList = updateDTOList.stream()
                                          .map(LargeHoldingsStkrtDTO::getSeq)
                                          .collect(Collectors.toSet())
                                          .stream()
                                          .collect(Collectors.toList());

        Map<Long, LargeHoldingsStkrtEntity> findUpdateEntityMap = new HashMap<>();
        for (List<Long> seqList : ListDivider.getDivisionList(distinctSeqList, 300)) {
            findUpdateEntityMap.putAll(queryFactory.selectFrom(largeHoldingsStkrtEntity)
                                                   .where(largeHoldingsStkrtEntity.seq.in(seqList))
                                                   .fetch()
                                                   .stream()
                                                   .collect(Collectors.toMap(LargeHoldingsStkrtEntity::getSeq, Function.identity(), (oldValue, newValue) -> newValue)));

        }

        List<LargeHoldingsStkrtEntity> updateEntityList = new ArrayList<>();

        for (LargeHoldingsStkrtDTO dto : updateDTOList) {
            LargeHoldingsStkrtEntity findUpdateEntity = findUpdateEntityMap.getOrDefault(dto.getSeq(), null);

            if (findUpdateEntity == null) {
                continue;
            }

            Optional<LargeHoldingsStkrtEntity.LargeHoldingsStkrtEntityBuilder> optional = Param.getSaveEntityToBuilder(dto, findUpdateEntity, findUpdateEntity.toBuilder());
            optional.ifPresent(value -> updateEntityList.add(value.build()));
        }

        largeHoldingsStkrtRepository.saveAll(updateEntityList);
    }

    private BooleanExpression corpCodeEq(String corpCode) {
        return corpCode != null ? largeHoldingsStkrtEntity.corpCode.eq(corpCode) : null;
    }

    private BooleanExpression rceptNoEq(String rceptNo) {
        return rceptNo != null ? largeHoldingsStkrtEntity.rceptNo.eq(rceptNo) : null;
    }

    private OrderSpecifier<?> dynamicOrder(String orderColumn, Boolean isDescending) {
        if (!hasText(orderColumn)) {
            return largeHoldingsStkrtEntity.rceptNo.asc();
        }

        if (isDescending == null) {
            isDescending = false;
        }

        Class<?> clazz = QLargeHoldingsStkrtEntity.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(largeHoldingsStkrtEntity.getType(), largeHoldingsStkrtEntity.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return largeHoldingsStkrtEntity.rceptNo.asc();
    }
}
