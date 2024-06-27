package com.example.szs.service;


import com.example.szs.domain.member.Member;
import com.example.szs.domain.tax.IncomeDeduction;
import com.example.szs.domain.tax.TaxInfo;
import com.example.szs.repository.member.MemberRepository;
import com.example.szs.repository.tax.TaxInfoRepository;
import com.example.szs.utils.TaxCalculator;
import com.example.szs.utils.money.TextChg;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RefundService {
    private final TaxInfoRepository taxInfoRepository;
    private final MemberRepository memberRepository;

    public Map<String, Object> getFinalTax(Long memberSeq, int year) {
        Member member = memberRepository.findById(memberSeq).get();
        TaxInfo taxInfo;
        if (year == -1) {
            taxInfo = taxInfoRepository.findByMember(member).get();
        } else {
            taxInfo = taxInfoRepository.findByMemberAndYear(member, year).get();
        }


        List<IncomeDeduction> incomeDeductionList = taxInfo.getIncomeDeductionList();
        BigDecimal totalIncomeDeductions = incomeDeductionList.stream().map(IncomeDeduction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        TaxCalculator taxCalculator = new TaxCalculator(taxInfo.getTotalInComeAmount(), totalIncomeDeductions, taxInfo.getTaxDeductionAmount());
        taxCalculator.calculatedTax();

        Map<String, Object> result = new HashMap<>();

        BigDecimal finalTax = taxCalculator.getFinalTax();
        result.put("결정세액", TextChg.formatBigDecimal(finalTax));
        return result;
    }
}
