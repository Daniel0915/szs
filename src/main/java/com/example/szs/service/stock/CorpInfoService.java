package com.example.szs.service.stock;

import com.example.szs.model.eNum.ResStatus;
import com.example.szs.module.ApiResponse;
import com.example.szs.repository.stock.CorpInfoRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CorpInfoService {
    private final CorpInfoRepositoryCustom corpInfoRepositoryCustom;
    private final ApiResponse apiResponse;

    public ResponseEntity<?> getAllCorpInfoDTOList() {
        return apiResponse.makeResponse(ResStatus.SUCCESS, corpInfoRepositoryCustom.getAllCorpInfoDTOList().stream().limit(5));
    }
}
