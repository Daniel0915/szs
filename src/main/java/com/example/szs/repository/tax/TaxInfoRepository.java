package com.example.szs.repository.tax;

import com.example.szs.domain.tax.TaxInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxInfoRepository extends JpaRepository<TaxInfo, Long> {
}
