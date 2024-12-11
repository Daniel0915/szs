package com.example.szs.repository.marketing;

import com.example.szs.domain.marketing.MarketingMng;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarketingMngRepository extends JpaRepository<MarketingMng, Long> {
    @Query("SELECT m FROM MarketingMng m WHERE m.userToken = :userToken")
    Optional<MarketingMng> getMarketingMngBy(@Param("userToken") String userToken);

    @Query("SELECT m FROM MarketingMng m WHERE m.pSeq = :pSeq ORDER BY m.page")
    List<MarketingMng> getMarketingMngByPSeq(@Param("pSeq") Long pSeq);

    @Query("SELECT m FROM MarketingMng m WHERE m.seq = :seq")
    Optional<MarketingMng> getMarketingMngBy(@Param("seq") long seq);

}
