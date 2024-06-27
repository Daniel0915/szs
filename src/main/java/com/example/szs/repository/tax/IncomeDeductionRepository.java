package com.example.szs.repository.tax;

import com.example.szs.domain.tax.IncomeDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IncomeDeductionRepository extends JpaRepository<IncomeDeduction, Long> {
}
