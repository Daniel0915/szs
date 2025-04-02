package com.example.szs.module.stock;

import com.example.szs.model.dto.largeHoldings.LargeHoldingsDTO;
import com.example.szs.model.eNum.ResStatus;
import com.example.szs.model.eNum.http.HeaderType;
import com.example.szs.module.ApiResponse;
import com.example.szs.module.client.RestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class LargeHoldings {
    private final RestClient restClient;
    private final ApiResponse apiResponse;
    private final String apiKey = "2QCYMHBJbeH4wsI/QY/zCfAL7O7plxWM";
    private final String serverUrl = "https://web-financial-server.onrender.com";

    @Autowired
    public LargeHoldings(RestClient restClient, ApiResponse apiResponse) {
        this.restClient = restClient;
        this.apiResponse = apiResponse;
    }

    public ResponseEntity<?> apiCallUpdateLargeHoldingsDetail(List<LargeHoldingsDTO> requestBody) {
        if (CollectionUtils.isEmpty(requestBody)) {
            return apiResponse.makeResponse(ResStatus.PARAM_REQUIRE_ERROR);
        }

        Map<String, Object> header = new HashMap<>(){{
            put(HeaderType.AUTH_HEADER.getCode(), apiKey);
        }};

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String paramDataJson = objectMapper.writeValueAsString(requestBody);
            String result = restClient.post(serverUrl + "/stock/update-scraping", paramDataJson, header);

            if (Objects.equals(result, ResStatus.HTTP_STATUS_4XX.getSCode())) {
                return apiResponse.makeResponse(ResStatus.HTTP_STATUS_4XX);
            }

            if (Objects.equals(result, ResStatus.HTTP_STATUS_5XX.getSCode())) {
                return apiResponse.makeResponse(ResStatus.HTTP_STATUS_5XX);
            }

            return apiResponse.makeResponse(ResStatus.SUCCESS);
        } catch (JsonProcessingException e) {
            log.error(" loginParamData object to json parse error or httpResult to mapping error - {} / {}", e.getMessage(), requestBody);
            return apiResponse.makeResponse(ResStatus.PARSING_ERROR);
        } catch (Exception e) {
            log.error(" restClient post error - {} / {}", e.getMessage(), requestBody);
            return apiResponse.makeResponse(ResStatus.SERVICE_ERROR);
        }
    }



}
