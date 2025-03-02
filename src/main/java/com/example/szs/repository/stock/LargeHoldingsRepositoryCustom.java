package com.example.szs.repository.stock;

import com.example.szs.domain.stock.QLargeHoldingsEntity;
import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
import com.example.szs.model.queryDSLSearch.LargeHoldingsSearchCondition;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import static com.example.szs.domain.stock.QLargeHoldingsEntity.largeHoldingsEntity;
import static org.springframework.util.StringUtils.hasText;

@Repository
@Slf4j
public class LargeHoldingsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public LargeHoldingsRepositoryCustom(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Optional<LargeHoldingsDTO> findLatestRecordBy(LargeHoldingsSearchCondition condition) {
        return Optional.ofNullable(queryFactory.selectFrom(largeHoldingsEntity)
                                               .where(
                                                       rceptNoEq    (condition.getRceptNo()),
                                                       corpCodeEq   (condition.getCorpCode())
                                               )
                                               .orderBy(dynamicOrder(condition))
                                               .fetchFirst())
                       .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, LargeHoldingsDTO.class));
    }

    private BooleanExpression rceptNoEq(String rceptNo) {
        return hasText(rceptNo) ? largeHoldingsEntity.rceptNo.eq(rceptNo) : null;
    }

    private BooleanExpression corpCodeEq(String corpCode) {
        return StringUtils.hasText(corpCode) ? largeHoldingsEntity.corpCode.eq(corpCode) : null;
    }

    private OrderSpecifier<?> dynamicOrder(LargeHoldingsSearchCondition condition) {
        if (!hasText(condition.getOrderColumn())) {
            return largeHoldingsEntity.rceptNo.asc();
        }

        String orderColumn = condition.getOrderColumn();
        boolean isDescending = condition.isDescending();

        Class<?> clazz = QLargeHoldingsEntity.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(largeHoldingsEntity.getType(), largeHoldingsEntity.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return largeHoldingsEntity.rceptNo.asc();
    }
}
