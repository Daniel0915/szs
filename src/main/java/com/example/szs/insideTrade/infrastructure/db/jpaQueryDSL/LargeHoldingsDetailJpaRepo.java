package com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL;

import com.example.szs.insideTrade.domain.LargeHoldingsDetail;
import com.example.szs.insideTrade.domain.LargeHoldingsDetailRepo;
import com.example.szs.utils.batch.JdbcBatchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsDetailJpaRepo implements LargeHoldingsDetailRepo {
    private final ILargeHoldingsDetailJpaRepo iLargeHoldingsDetailJpaRepo;
    private final JdbcTemplate                jdbcTemplate;

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
}
