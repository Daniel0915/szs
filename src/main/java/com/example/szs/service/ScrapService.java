package com.example.szs.service;

import com.example.szs.domain.member.Member;
import com.example.szs.domain.tax.IncomeDeduction;
import com.example.szs.domain.tax.TaxInfo;
import com.example.szs.eNum.IncomeDeductionType;
import com.example.szs.module.client.RestClient;
import com.example.szs.repository.member.MemberRepository;
import com.example.szs.repository.tax.IncomeDeductionRepository;
import com.example.szs.repository.tax.TaxInfoRepository;
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
import java.time.YearMonth;
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

    // TODO : saveAll(단건 + 한번에 transactional) 대신 insert bulk 적용
    @Transactional
    public void insertScrapInfo(Long memberSeq) throws ParseException {
        // 외부 호출
        Member member = memberRepository.findById(memberSeq).get();
        String name = member.getName();
        String regNo = member.getRegNo();

        // TODO : 테스트 데이터라서 삭제해야함.
//        String name = "동탁";
//        String regNo = "921108-1582816";

        Map<String, Object> scrapInfo = this.getScrapInfo(name, regNo);

        Map<String, Object> incomeDeduction = (Map<String, Object>) scrapInfo.get("소득공제");

        // TODO : 금액 변경해주는 util 만들어야할 거 같음.
        BigDecimal totalInComeAmount = new BigDecimal((Integer) scrapInfo.get("종합소득금액"));
        BigDecimal taxDeductionAmount = new BigDecimal((Integer) incomeDeduction.get("세액공제"));

        List<Map<String, Object>> gugminIncomeDeductionList = (List<Map<String, Object>>) incomeDeduction.get("국민연금");

        Map<String, Object> creditCardIncomeDeduction = (Map<String, Object>) incomeDeduction.get("신용카드소득공제");
        // 세금 처리 년도
        int year = (Integer) creditCardIncomeDeduction.get("year");


        TaxInfo taxInfo = taxInfoRepository.save(TaxInfo.builder()
                                                        .member(member)
                                                        .year(year)
                                                        .totalInComeAmount(totalInComeAmount)
                                                        .taxDeductionAmount(taxDeductionAmount)
                                                        .build());

        List<IncomeDeduction> gugminIncomeDeductionListEntity = this.convetToGugminIncomeDeductionList(gugminIncomeDeductionList, taxInfo);
        incomeDeductionRepository.saveAll(gugminIncomeDeductionListEntity);

        List<IncomeDeduction> creditCardIncomeDeductionListEntity = this.convetToCreditCardIncomeDeductionList(creditCardIncomeDeduction, taxInfo);
        incomeDeductionRepository.saveAll(creditCardIncomeDeductionListEntity);
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
}
