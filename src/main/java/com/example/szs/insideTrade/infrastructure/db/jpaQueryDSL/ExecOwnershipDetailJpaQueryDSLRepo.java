package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.ExecOwnershipDetail;
import com.example.szs.insideTrade.domain.ExecOwnershipDetailRepo;
import com.example.szs.utils.batch.JdbcBatchUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ExecOwnershipDetailJpaQueryDSLRepo implements ExecOwnershipDetailRepo {
    private final IExecOwnershipDetailJpaRepo iExecOwnershipDetailJpaRepo;
    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate    jdbcTemplate;

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
            ps.setString(5, entity.getIsuExctvOfcps());
            ps.setString(6, entity.getIsuMainShrholdr());
            ps.setString(7, entity.getTradeDt());
            ps.setString(8, entity.getTradeReason());
            ps.setString(9, entity.getStockType());
            ps.setObject(10, entity.getBeforeStockAmount());
            ps.setObject(11, entity.getChangeStockAmount());
            ps.setObject(12, entity.getAfterStockAmount());
            ps.setString(13, entity.getUnitStockPrice());
            ps.setString(14, entity.getRegDt());
        });
    }
}
