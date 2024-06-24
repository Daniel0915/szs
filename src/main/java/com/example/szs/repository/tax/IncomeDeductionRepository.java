package com.example.szs.repository.tax;

import com.example.szs.domain.tax.IncomeDeduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeDeductionRepository extends JpaRepository<IncomeDeduction, Long> {
}
