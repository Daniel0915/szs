package com.example.szs.common.utils.batch;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JdbcBatchUtil {
    /**
     * 공통 배치 실행 메서드
     *
     * @param jdbcTemplate JdbcTemplate
     * @param list         배치할 데이터 리스트
     * @param batchSize    배치 단위
     * @param sql          실행할 SQL
     * @param setter       각 아이템을 PreparedStatement에 설정하는 함수
     * @param <T>          데이터 타입
     */
    public static <T> void executeBatch(JdbcTemplate jdbcTemplate, List<T> list, int batchSize, String sql, BatchSetter<T> setter) {
        for (int start = 0; start < list.size(); start += batchSize) {
            int end = Math.min(start + batchSize, list.size());
            List<T> batch = list.subList(start, end);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    setter.set(ps, batch.get(i));
                }

                @Override
                public int getBatchSize() {
                    return batch.size();
                }
            });
        }
    }

    @FunctionalInterface
    public interface BatchSetter<T> {
        void set(PreparedStatement ps, T item) throws SQLException;
    }
}
