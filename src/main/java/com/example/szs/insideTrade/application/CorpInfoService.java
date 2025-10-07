package com.example.szs.service.stock;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.example.szs.insideTrade.presentation.dto.response.CorpInfoResDTO;
import com.example.szs.model.eNum.ResStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CorpInfoService {
    private final CorpInfoRepo corpInfoRepo;

    // TODO : 캐싱 처리
    public List<CorpInfoResDTO> getAllCorpInfoDTOList() {
        List<CorpInfo> corpInfoList = corpInfoRepo.findAll();
        List<CorpInfoResDTO> response = new ArrayList<>(corpInfoList.size());

        for (CorpInfo corpInfo : corpInfoList) {
            response.add(CorpInfoResDTO.builder()
                                       .corpCode(corpInfo.getCorpCode())
                                       .corpName(corpInfo.getCorpName())
                                       .build());
        }

        return response;
    }
}
