//package com.example.szs.repository.stock;
//
//import com.example.szs.domain.stock.QExecOwnershipEntity;
//import com.example.szs.model.dto.execOwnership.ExecOwnershipDTO;
//import com.example.szs.model.queryDSLSearch.ExecOwnershipSearchCondition;
//import com.example.szs.utils.jpa.EntityToDtoMapper;
//import com.example.szs.utils.jpa.ListDivider;
//import com.querydsl.core.types.OrderSpecifier;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.core.types.dsl.PathBuilder;
//import com.querydsl.core.types.dsl.StringPath;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import java.lang.reflect.Field;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.example.szs.domain.stock.QExecOwnershipEntity.execOwnershipEntity;
//import static org.springframework.util.StringUtils.hasText;
//
//@Repository
//@Slf4j
//public class ExecOwnershipRepositoryCustom {
//    private final JPAQueryFactory queryFactory;
//
//    public ExecOwnershipRepositoryCustom(EntityManager em) {
//        this.queryFactory = new JPAQueryFactory(em);
//    }
//
//    public Optional<ExecOwnershipDTO> findLatestRecordBy(ExecOwnershipSearchCondition condition) {
//        return Optional.ofNullable(queryFactory.selectFrom(execOwnershipEntity)
//                                               .where(
//                                                       rceptNoEq    (condition.getRceptNo()),
//                                                       corpCodeEq   (condition.getCorpCode())
//                                               )
//                                               .orderBy(dynamicOrder(condition))
//                                               .fetchFirst())
//                       .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, ExecOwnershipDTO.class));
//    }
//
//    private BooleanExpression rceptNoEq(String rceptNo) {
//        return hasText(rceptNo) ? execOwnershipEntity.rceptNo.eq(rceptNo) : null;
//    }
//
//    public List<ExecOwnershipDTO> getExecOwnershipOrderSpStockLmpCnt(String corpCode) {
//        if (!StringUtils.hasText(corpCode)) {
//            return new ArrayList<>();
//        }
//
//        List<String> reprorList = queryFactory.select(execOwnershipEntity.repror) // 반환 타입을 지정.from(execOwnershipEntity)
//                                              .from(execOwnershipEntity)
//                                              .where(corpCodeEq(corpCode))
//                                              .groupBy(execOwnershipEntity.repror)
//                                              .fetch();
//        if (CollectionUtils.isEmpty(reprorList)) {
//            return new ArrayList<>();
//        }
//
//        List<String> recentRceptNoList = new ArrayList<>();
//        for (List<String> divisionReprorList : ListDivider.getDivisionList(reprorList, 300)) {
//            recentRceptNoList.addAll(queryFactory
//                    .select(execOwnershipEntity.rceptNo.max())
//                    .from(execOwnershipEntity)
//                    .where(execOwnershipEntity.repror.in(divisionReprorList))
//                    .groupBy(execOwnershipEntity.repror)
//                    .fetch());
//        }
//
//        if (CollectionUtils.isEmpty(recentRceptNoList)) {
//            return new ArrayList<>();
//        }
//
//        List<ExecOwnershipDTO> execOwnershipDTOList = new ArrayList<>();
//
//        for (List<String> divisionRceptNoList : ListDivider.getDivisionList(recentRceptNoList, 300)) {
//            execOwnershipDTOList.addAll(queryFactory.selectFrom(execOwnershipEntity)
//                                                    .where(execOwnershipEntity.rceptNo.in(divisionRceptNoList))
//                                                    .fetch()
//                                                    .stream()
//                                                    .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, ExecOwnershipDTO.class).stream())
//                                                    .collect(Collectors.toList()));
//        }
//
//        if (CollectionUtils.isEmpty(execOwnershipDTOList)) {
//            return new ArrayList<>();
//        }
//
//        return execOwnershipDTOList.stream().sorted(Comparator.comparing(ExecOwnershipDTO::getSpStockLmpCnt).reversed()).collect(Collectors.toList());
//    }
//
//    private BooleanExpression corpCodeEq(String corpCode) {
//        return hasText(corpCode) ? execOwnershipEntity.corpCode.eq(corpCode) : null;
//    }
//
//    private OrderSpecifier<?> dynamicOrder(ExecOwnershipSearchCondition condition) {
//        if (!hasText(condition.getOrderColumn())) {
//            return execOwnershipEntity.rceptNo.asc();
//        }
//
//        String orderColumn = condition.getOrderColumn();
//        boolean isDescending = condition.isDescending();
//
//        Class<?> clazz = QExecOwnershipEntity.class;
//
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            if (Objects.equals(orderColumn, field.getName())) {
//                // PathBuilder를 사용하여 필드 동적 참조
//                PathBuilder<?> pathBuilder = new PathBuilder<>(execOwnershipEntity.getType(), execOwnershipEntity.getMetadata());
//
//                StringPath path = pathBuilder.getString(field.getName());
//                return isDescending ? path.desc() : path.asc();
//            }
//        }
//
//        // 필드 네임을 찾지 못하면 pk 오름차순
//        return execOwnershipEntity.rceptNo.asc();
//    }
//}
