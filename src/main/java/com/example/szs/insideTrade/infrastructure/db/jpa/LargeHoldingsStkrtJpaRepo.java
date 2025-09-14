package com.example.szs.insideTrade.infrastructure.db.jpa;

import com.example.szs.insideTrade.domain.LargeHoldingsStkrt;
import com.example.szs.insideTrade.domain.LargeHoldingsStkrtRepo;
import com.example.szs.utils.batch.JdbcBatchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsStkrtJpaRepo implements LargeHoldingsStkrtRepo {
    private final ILargeHoldingsStkrtJpaRepo iLargeHoldingsStkrtJpaRepo;
    private final JdbcTemplate               jdbcTemplate;

    @Override
    public List<LargeHoldingsStkrt> saveAll(List<LargeHoldingsStkrt> entities) {
        return iLargeHoldingsStkrtJpaRepo.saveAll(entities);
    }

    @Override
    public void insertNativeBatch(List<LargeHoldingsStkrt> entities, int batchSize) {
        String sql = """
            INSERT INTO large_holdings_stkrt
                (rcept_no, corp_code, corp_name, large_holdings_name, birth_date_or_biz_reg_num,
                 total_stock_amount, stkrt, reg_dt)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        JdbcBatchUtil.executeBatch(jdbcTemplate, entities, batchSize, sql, (ps, entity) -> {
            ps.setString(1, entity.getRceptNo());
            ps.setString(2, entity.getCorpCode());
            ps.setString(3, entity.getCorpName());
            ps.setString(4, entity.getLargeHoldingsName());
            ps.setString(5, entity.getBirthDateOrBizRegNum());
            ps.setObject(6, entity.getTotalStockAmount());
            ps.setObject(7, entity.getStkrt());
            ps.setString(8, entity.getRegDt());
        });
    }
}
