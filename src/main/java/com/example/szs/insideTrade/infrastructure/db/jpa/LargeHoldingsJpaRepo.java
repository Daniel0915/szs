package com.example.szs.insideTrade.infrastructure.db.jpa;

import com.example.szs.insideTrade.domain.LargeHoldings;
import com.example.szs.insideTrade.domain.LargeHoldingsRepo;
import com.example.szs.insideTrade.infrastructure.db.queryDSL.LargeHoldingsSearchCondition;
import com.example.szs.utils.batch.JdbcBatchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LargeHoldingsJpaRepo implements LargeHoldingsRepo {
    private final ILargeHoldingsJpaRepo iLargeHoldingsJpaRepo;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<LargeHoldings> findLatestRecordBy(LargeHoldingsSearchCondition searchCondition) {
        assert false : "QueryDSL 에서 사용합니다. 필요 시, 구현해주세요.";
        return Optional.empty();
    }

    @Override
    public List<LargeHoldings> saveAll(List<LargeHoldings> largeHoldings) {
        return iLargeHoldingsJpaRepo.saveAll(largeHoldings);
    }

    @Override
    public void insertNativeBatch(List<LargeHoldings> largeHoldings, int batchSize) throws Exception {
        // TODO : 테스트 코드
        log.info("jdbcTemplate 메모리 위치 확인, jdbcTemplate : {}", jdbcTemplate);
        // TODO : 테스트 코드

        String sql = """
                INSERT INTO large_holdings
                    (rcept_no, corp_code, corp_name, repror, stkqy, stkqy_irds, stkrt, stkrt_irds, report_resn, rcept_dt, reg_dt)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;


        JdbcBatchUtil.executeBatch(jdbcTemplate, largeHoldings, batchSize, sql, (ps, largeHolding) -> {
            ps.setString(1, largeHolding.getRceptNo());
            ps.setString(2, largeHolding.getCorpCode());
            ps.setString(3, largeHolding.getCorpName());
            ps.setString(4, largeHolding.getRepror());
            ps.setObject(5, largeHolding.getStkqy());
            ps.setObject(6, largeHolding.getStkqyIrds());
            ps.setObject(7, largeHolding.getStkrt());
            ps.setObject(8, largeHolding.getStkrtIrds());
            ps.setString(9, largeHolding.getReportResn());
            ps.setString(10, largeHolding.getRceptDt());
            ps.setString(11, largeHolding.getRegDt());
        });
    }
}
