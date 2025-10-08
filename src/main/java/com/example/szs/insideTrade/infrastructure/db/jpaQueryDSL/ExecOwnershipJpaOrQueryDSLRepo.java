package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.ExecOwnership;
import com.example.szs.insideTrade.domain.ExecOwnershipRepo;
import com.example.szs.insideTrade.domain.QExecOwnership;
import com.example.szs.insideTrade.application.dto.ExecOwnershipDTO;
import com.example.szs.insideTrade.presentation.dto.request.ExecOwnershipSearchCondition;
import com.example.szs.common.utils.batch.JdbcBatchUtil;
import com.example.szs.common.utils.jpa.EntityToDtoMapper;
import com.example.szs.common.utils.jpa.ListDivider;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;
import static com.example.szs.insideTrade.domain.QExecOwnership.execOwnership;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ExecOwnershipJpaOrQueryDSLRepo implements ExecOwnershipRepo {
    private final IExecOwnershipJpaRepo iExecOwnershipJpaRepo;
    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ExecOwnershipDTO> findLatestRecordBy(ExecOwnershipSearchCondition condition) {
        return Optional.ofNullable(queryFactory.selectFrom(execOwnership)
                                               .where(
                                                       rceptNoEq    (condition.getRceptNo()),
                                                       corpCodeEq   (condition.getCorpCode())
                                               )
                                               .orderBy(dynamicOrder(condition))
                                               .fetchFirst())
                       .flatMap(entity -> EntityToDtoMapper.mapEntityToDto(entity, ExecOwnershipDTO.class));
    }

    @Override
    public List<ExecOwnership> getExecOwnershipOrderSpStockLmpCnt(String corpCode) {
        if (!StringUtils.hasText(corpCode)) {
            return new ArrayList<>();
        }

        List<String> reprorList = queryFactory.select(execOwnership.repror)
                                              .from(execOwnership)
                                              .where(corpCodeEq(corpCode))
                                              .groupBy(execOwnership.repror)
                                              .fetch();
        if (CollectionUtils.isEmpty(reprorList)) {
            return new ArrayList<>();
        }

        List<String> recentRceptNoList = new ArrayList<>();
        for (List<String> divisionReprorList : ListDivider.getDivisionList(reprorList, 300)) {
            recentRceptNoList.addAll(queryFactory
                    .select(execOwnership.rceptNo.max())
                    .from(execOwnership)
                    .where(execOwnership.repror.in(divisionReprorList))
                    .groupBy(execOwnership.repror)
                    .fetch());
        }

        if (CollectionUtils.isEmpty(recentRceptNoList)) {
            return new ArrayList<>();
        }

        List<ExecOwnership> execOwnershipList = new ArrayList<>();

        for (List<String> divisionRceptNoList : ListDivider.getDivisionList(recentRceptNoList, 300)) {
            execOwnershipList.addAll(
                    queryFactory.selectFrom(execOwnership)
                                .where(execOwnership.rceptNo.in(divisionRceptNoList))
                                .fetch()
            );
        }

        if (CollectionUtils.isEmpty(execOwnershipList)) {
            return new ArrayList<>();
        }

        return execOwnershipList.stream()
                                .sorted(Comparator.comparing(ExecOwnership::getSpStockLmpCnt).reversed())
                                .collect(Collectors.toList());
    }

    @Override
    public void insertNativeBatch(List<ExecOwnership> entities, int batchSize) {
        String sql = """
                INSERT INTO public.exec_ownership
                (rcept_no, corp_code, corp_name, repror, isu_exctv_rgist_at, isu_exctv_ofcps,
                isu_main_shrholdr, sp_stock_lmp_cnt, sp_stock_lmp_irds_cnt, sp_stock_lmp_rate,
                sp_stock_lmp_irds_rate, rcept_dt, reg_dt)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        JdbcBatchUtil.executeBatch(jdbcTemplate, entities, batchSize, sql, (ps, entity) -> {
            ps.setString(1, entity.getRceptNo());
            ps.setString(2, entity.getCorpCode());
            ps.setString(3, entity.getCorpName());
            ps.setString(4, entity.getRepror());
            ps.setString(5, entity.getIsuExctvRgistAt());
            ps.setString(6, entity.getIsuExctvOfcps());
            ps.setString(7, entity.getIsuMainShrholdr());
            ps.setObject(8, entity.getSpStockLmpCnt());
            ps.setObject(9, entity.getSpStockLmpIrdsCnt());
            ps.setObject(10, entity.getSpStockLmpRate());
            ps.setObject(11, entity.getSpStockLmpIrdsRate());
            ps.setString(12, entity.getRceptDt());
            ps.setString(13, entity.getRegDt());
        });
    }

    private BooleanExpression rceptNoEq(String rceptNo) {
        return hasText(rceptNo) ? execOwnership.rceptNo.eq(rceptNo) : null;
    }

    private BooleanExpression corpCodeEq(String corpCode) {
        return hasText(corpCode) ? execOwnership.corpCode.eq(corpCode) : null;
    }

    private OrderSpecifier<?> dynamicOrder(ExecOwnershipSearchCondition condition) {
        if (!hasText(condition.getOrderColumn())) {
            return execOwnership.rceptNo.asc();
        }

        String orderColumn = condition.getOrderColumn();
        boolean isDescending = condition.isDescending();

        Class<?> clazz = QExecOwnership.class;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.equals(orderColumn, field.getName())) {
                // PathBuilder를 사용하여 필드 동적 참조
                PathBuilder<?> pathBuilder = new PathBuilder<>(execOwnership.getType(), execOwnership.getMetadata());

                StringPath path = pathBuilder.getString(field.getName());
                return isDescending ? path.desc() : path.asc();
            }
        }

        // 필드 네임을 찾지 못하면 pk 오름차순
        return execOwnership.rceptNo.asc();
    }
}
