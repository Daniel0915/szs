package com.example.szs.repository.stock;

import com.example.szs.domain.stock.QExecOwnershipEntity;
import com.example.szs.model.dto.ExecOwnershipDTO;
import com.example.szs.model.queryDSLSearch.ExecOwnershipSearchCondition;
import com.example.szs.utils.jpa.EntityToDtoMapper;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import static com.example.szs.domain.stock.QExecOwnershipEntity.execOwnershipEntity;
import static org.springframework.util.StringUtils.hasText;

@Repository
@Slf4j
public class ExecOwnershipRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ExecOwnershipRepositoryCustom(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Optional<ExecOwnershipDTO> findLatestRecordBy(ExecOwnershipSearchCondition condition) {
        return Optional.ofNullable(queryFactory.selectFrom(execOwnershipEntity)
                                               .where(
                                                       rceptNoEq    (condition.getRceptNo()),
                                                       corpCodeEq   (condition.getCorpCode())
                                               )
                                               .orderBy(dynamicOrder(condition))
                                               .fetchFirst())
                       .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, ExecOwnershipDTO.class));
    }

    private BooleanExpression rceptNoEq(String rceptNo) {
        return hasText(rceptNo) ? execOwnershipEntity.rceptNo.eq(rceptNo) : null;
    }

    private BooleanExpression corpCodeEq(Long corpCode) {
        return corpCode != null ? execOwnershipEntity.corpCode.eq(corpCode) : null;
    }

    private OrderSpecifier<?> dynamicOrder(ExecOwnershipSearchCondition condition) {
        if (!hasText(condition.getOrderColumn())) {
            return execOwnershipEntity.rceptNo.asc();
        }

        String orderColumn = condition.getOrderColumn();
        boolean isDescending = condition.isDescending();

        Class<?> clazz = QExecOwnershipEntity.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(execOwnershipEntity.getType(), execOwnershipEntity.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return execOwnershipEntity.rceptNo.asc();
    }
}
