package com.example.szs.insideTrade.domain;

import com.example.szs.insideTrade.infrastructure.client.Dart;
import com.example.szs.insideTrade.infrastructure.client.dto.ExecOwnershipInsiderTradeApiRes;
import com.example.szs.insideTrade.application.dto.ExecOwnershipDTO;
import com.example.szs.model.queryDSLSearch.ExecOwnershipSearchCondition;
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
public class ExecOwnershipDomainService {
    private final Dart dart;
    private final ExecOwnershipRepo execOwnershipRepo;

    @Transactional(rollbackFor = Exception.class)
    public List<ExecOwnership> saveRecentLExecOwnership(CorpInfo corpInfo) throws Exception {
        Optional<ExecOwnershipInsiderTradeApiRes> resOptional = dart.findExecOwnershipInsiderTrade(corpInfo);

        if (resOptional.isEmpty() || CollectionUtils.isEmpty(resOptional.get().getList())) {
            return new ArrayList<>();
        }

        List<ExecOwnershipInsiderTradeApiRes.ExecOwnership> resList = resOptional.get().getList();

        // 가장 최근 내부 DB에 저장된 지분 공시 데이터 조회
        Optional<ExecOwnershipDTO> optionalExecOwnership = execOwnershipRepo.findLatestRecordBy(ExecOwnershipSearchCondition.builder()
                                                                                                                            .corpCode(corpInfo.getCorpCode())
                                                                                                                            .orderColumn(ExecOwnership.Fields.rceptNo)
                                                                                                                            .isDescending(true)
                                                                                                                            .build());

        // TODO : 외부 호출 과 내부 DB 데이터 비교 후, 내부 DB 에 없는 데이터 저장
        List<ExecOwnershipInsiderTradeApiRes.ExecOwnership> execOwnershipList = new ArrayList<>();
        if (optionalExecOwnership.isPresent()) {
            String lastRceptNo = optionalExecOwnership.get().getRceptNo();
            int startIndex = IntStream.range(0, resList.size())
                                      .filter(index -> Objects.equals(resList.get(index).getRceptNo(), lastRceptNo))
                                      .findFirst()
                                      .orElse(-1);

            int skipCount = (startIndex == -1) ? 0 : startIndex + 1;
            execOwnershipList = resList.stream()
                                      .skip(skipCount)
                                      .collect(Collectors.toCollection(() -> new ArrayList<>(resList.size())));
        } else {
            execOwnershipList = resList;
        }

        int capacity = execOwnershipList.size();
        List<ExecOwnership> insertList = execOwnershipList.stream()
                                                          .map(ExecOwnership::create)
                                                          .collect(Collectors.toCollection(() -> new ArrayList<>(capacity)));

        execOwnershipRepo.insertNativeBatch(insertList, 500);
        return insertList;
    }




}
