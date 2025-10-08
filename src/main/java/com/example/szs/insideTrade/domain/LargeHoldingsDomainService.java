package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.insideTrade.infrastructure.client.dto.LargeHoldingsInsiderTradeApiRes;
import com.example.szs.insideTrade.infrastructure.db.jpaQueryDSL.LargeHoldingsSearchCondition;
import com.example.szs.common.utils.money.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LargeHoldingsDomainService {
    private final LargeHoldingsRepo largeHoldingsRepo;
    private final Dart              dart;

    @Transactional(rollbackFor = Exception.class)
    public List<LargeHoldings> saveRecentLargeHoldings(CorpInfo corpInfo) throws Exception {
        // 회사별 지분공시 외부 Dart 호출로 조회 [외부 호출]
        Optional<LargeHoldingsInsiderTradeApiRes> resOptional = dart.findLargeHoldingsInsiderTrade(corpInfo);

        if (resOptional.isEmpty() || CollectionUtils.isEmpty(resOptional.get().getList())) {
            return new ArrayList<>();
        }

        List<LargeHoldingsInsiderTradeApiRes.LargeHolding> resList = resOptional.get().getList();

        // 가장 최근 내부 DB에 저장된 지분 공시 데이터 조회
        Optional<LargeHoldings> optionalLargeHoldings = largeHoldingsRepo.findLatestRecordBy(LargeHoldingsSearchCondition.builder()
                                                                                                                         .corpCode(corpInfo.getCorpCode())
                                                                                                                         .orderColumn(LargeHoldings.Fields.rceptNo)
                                                                                                                         .isDescending(true)
                                                                                                                         .build());
        // 외부 호출 과 내부 DB 데이터 비교 후, 내부 DB 에 없는 데이터 저장
        List<LargeHoldingsInsiderTradeApiRes.LargeHolding> largeHoldingList = new ArrayList<>();
        if (optionalLargeHoldings.isPresent()) {
            String lastRceptNo = optionalLargeHoldings.get().getRceptNo();
            int startIndex = IntStream.range(0, resList.size())
                                      .filter(index -> Objects.equals(resList.get(index).getRceptNo(), lastRceptNo))
                                      .findFirst()
                                      .orElse(-1);

            int skipCount = (startIndex == -1) ? 0 : startIndex + 1;
            largeHoldingList = resList.stream()
                                      .skip(skipCount)
                                      .collect(Collectors.toList());
        } else {
            largeHoldingList = resList;
        }

        List<LargeHoldings> insertList = largeHoldingList.stream()
                                                         .map(largeHolding -> LargeHoldings.create(
                                                                 largeHolding.getRceptNo(),
                                                                 largeHolding.getCorpCode(),
                                                                 largeHolding.getCorpName(),
                                                                 largeHolding.getRepror(),
                                                                 NumberUtils.stringToLongConverter(largeHolding.getStkqy()),
                                                                 NumberUtils.stringToLongConverter(largeHolding.getStkqyIrds()),
                                                                 NumberUtils.stringToFloatConverter(largeHolding.getStkrt()),
                                                                 NumberUtils.stringToFloatConverter(largeHolding.getStkrtIrds()),
                                                                 largeHolding.getReportResn(),
                                                                 largeHolding.getRceptDt()
                                                         ))
                                                         .collect(Collectors.toList());

        largeHoldingsRepo.insertNativeBatch(insertList, 500);
        return insertList;

    }
}
