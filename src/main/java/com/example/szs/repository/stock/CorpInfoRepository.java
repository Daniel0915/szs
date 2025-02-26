package com.example.szs.repository.stock;

import com.example.szs.domain.stock.CorpInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorpInfoRepository extends JpaRepository<CorpInfoEntity, String> {
}
