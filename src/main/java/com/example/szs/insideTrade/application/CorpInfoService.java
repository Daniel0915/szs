package com.example.szs.service.stock;

import com.example.szs.insideTrade.domain.CorpInfo;
import com.example.szs.insideTrade.domain.CorpInfoRepo;
import com.example.szs.insideTrade.presentation.dto.response.CorpInfoResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CorpInfoService {
    private final CorpInfoRepo corpInfoRepo;

    // TODO : 캐싱 처리
    // TODO : 테스트 코드
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
