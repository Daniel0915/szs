package com.example.szs.service;

import com.example.szs.domain.embedded.Time;
import com.example.szs.domain.member.Member;
import com.example.szs.domain.tax.IncomeDeduction;
import com.example.szs.domain.tax.TaxInfo;
import com.example.szs.model.eNum.IncomeDeductionType;
import com.example.szs.module.client.RestClient;
import com.example.szs.repository.member.MemberRepository;
import com.example.szs.repository.tax.IncomeDeductionRepository;
import com.example.szs.repository.tax.TaxInfoRepository;
import com.example.szs.utils.encryption.EncryptDecryptUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ScrapService {
    @Value("${scrap.url}")
    private String scrapUrl;
    @Value("${scrap.header.key}")
    private String scrapHeaderKey;
    @Value("${scrap.header.value}")
    private String scrapHeaderValue;

    private final RestClient restClient;
    private final MemberRepository memberRepository;
    private final TaxInfoRepository taxInfoRepository;
    private final IncomeDeductionRepository incomeDeductionRepository;

    @Transactional
    public void insertScrapInfo(Long memberSeq) throws Exception {
        // 외부 호출
        Member member = memberRepository.findById(memberSeq).get();
        String name = member.getName();
        String regNo = EncryptDecryptUtils.decrypt(member.getRegNo());

        Map<String, Object> scrapInfo = this.getScrapInfo(name, regNo);

        Map<String, Object> incomeDeduction = (Map<String, Object>) scrapInfo.get("소득공제");

        BigDecimal totalInComeAmount = new BigDecimal((Integer) scrapInfo.get("종합소득금액"));
        BigDecimal taxDeductionAmount = new BigDecimal((Integer) incomeDeduction.get("세액공제"));

        List<Map<String, Object>> gugminIncomeDeductionList = (List<Map<String, Object>>) incomeDeduction.get("국민연금");

        Map<String, Object> creditCardIncomeDeduction = (Map<String, Object>) incomeDeduction.get("신용카드소득공제");
        // 세금 처리 년도
        int year = (Integer) creditCardIncomeDeduction.get("year");

        Optional<TaxInfo> findTaxInfo = taxInfoRepository.findByMemberAndYear(member, year);

        if (!findTaxInfo.isPresent()) {
            createTaxInfoAndIncomeDeduction(member, year, totalInComeAmount, taxDeductionAmount, gugminIncomeDeductionList, creditCardIncomeDeduction);
            return;
        }

        updateTaxInfoAndIncomeDeduction(member, findTaxInfo.get(), year, totalInComeAmount, taxDeductionAmount, gugminIncomeDeductionList, creditCardIncomeDeduction);
    }

    private Map<String, Object> getScrapInfo(String name, String regNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("regNo", regNo);

        Map<String, Object> header = new HashMap<>();
        header.put(scrapHeaderKey, scrapHeaderValue);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> result;

        try {
            String paramDataJson = objectMapper.writeValueAsString(param);
            String httpResult = restClient.post(scrapUrl, paramDataJson, header);
            result = objectMapper.readValue(httpResult, Map.class);
        } catch (JsonProcessingException e) {
            log.error(" loginParamData object to json parse error or httpResult to mapping error - {} / {}", e.getMessage(), param);
            return new HashMap<>();
        } catch (Exception e) {
            log.error(" restClient post error - {} / {}", e.getMessage(), param);
            return new HashMap<>();
        }
        return (Map<String, Object>) result.get("data");
    }

    private List<IncomeDeduction> convetToGugminIncomeDeductionList(List<Map<String, Object>> gugminIncomeDeductionList, TaxInfo taxInfo) throws ParseException {
        List<IncomeDeduction> incomeDeductionList = new ArrayList<>();

        for (Map<String, Object> map : gugminIncomeDeductionList) {
            BigDecimal amount = new BigDecimal(NumberFormat.getInstance(Locale.getDefault()).parse((String) map.get("공제액")).toString());
            String yearMonth = (String) map.get("월");

            incomeDeductionList.add(IncomeDeduction.builder()
                                                   .taxInfo(taxInfo)
                                                   .type(IncomeDeductionType.G)
                                                   .amount(amount)
                                                   .yearMonth(yearMonth)
                                                   .build());
        }
        return incomeDeductionList;
    }

    private List<IncomeDeduction> convetToCreditCardIncomeDeductionList(Map<String, Object> creditCardIncomeDeduction, TaxInfo taxInfo) throws ParseException {
        List<IncomeDeduction> incomeDeductionList = new ArrayList<>();

        String year = String.valueOf(creditCardIncomeDeduction.get("year"));
        List<Map<String, Object>> creditCardAmountList = (List<Map<String, Object>>) creditCardIncomeDeduction.get("month");

        for (Map<String, Object> map : creditCardAmountList) {
            BigDecimal amount = BigDecimal.ZERO;
            String yearMonth = "";

            for (String key : map.keySet()) {
                yearMonth = year + "-" + key;
                amount = new BigDecimal(NumberFormat.getInstance(Locale.getDefault()).parse((String) map.get(key)).toString());
            }

            incomeDeductionList.add(IncomeDeduction.builder()
                                                   .taxInfo(taxInfo)
                                                   .type(IncomeDeductionType.S)
                                                   .amount(amount)
                                                   .yearMonth(yearMonth)
                                                   .build());
        }

        return incomeDeductionList;
    }

    private void createTaxInfoAndIncomeDeduction(Member member, int year, BigDecimal totalInComeAmount, BigDecimal taxDeductionAmount, List<Map<String, Object>> gugminIncomeDeductionList, Map<String, Object> creditCardIncomeDeduction) throws ParseException {
        TaxInfo taxInfo = taxInfoRepository.save(TaxInfo.builder()
                                                        .member(member)
                                                        .year(year)
                                                        .totalInComeAmount(totalInComeAmount)
                                                        .taxDeductionAmount(taxDeductionAmount)
                                                        .time(new Time())
                                                        .build());

        createIncomeDeduction(gugminIncomeDeductionList, creditCardIncomeDeduction, taxInfo);
    }
    private void updateTaxInfoAndIncomeDeduction(Member member, TaxInfo taxInfo, int year, BigDecimal totalInComeAmount, BigDecimal taxDeductionAmount, List<Map<String, Object>> gugminIncomeDeductionList, Map<String, Object> creditCardIncomeDeduction) throws ParseException {
        incomeDeductionRepository.deleteByTaxInfoSeq(member.getSeq());

        TaxInfo updateTaxInfo = taxInfo.toBuilder()
                                       .member(member)
                                       .year(year)
                                       .totalInComeAmount(totalInComeAmount)
                                       .taxDeductionAmount(taxDeductionAmount)
                                       .build();

        updateTaxInfo.getTime().updateModDt();
        taxInfoRepository.save(updateTaxInfo);

        createIncomeDeduction(gugminIncomeDeductionList, creditCardIncomeDeduction, updateTaxInfo);
    }

    private void createIncomeDeduction(List<Map<String, Object>> gugminIncomeDeductionList, Map<String, Object> creditCardIncomeDeduction, TaxInfo taxInfo) throws ParseException {
        List<IncomeDeduction> gugminIncomeDeductionListEntity = this.convetToGugminIncomeDeductionList(gugminIncomeDeductionList, taxInfo);
        incomeDeductionRepository.saveAll(gugminIncomeDeductionListEntity);

        List<IncomeDeduction> creditCardIncomeDeductionListEntity = this.convetToCreditCardIncomeDeductionList(creditCardIncomeDeduction, taxInfo);
        incomeDeductionRepository.saveAll(creditCardIncomeDeductionListEntity);
    }
}
