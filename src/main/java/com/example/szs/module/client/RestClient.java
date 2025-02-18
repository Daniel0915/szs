package com.example.szs.module.client;

import com.example.szs.model.eNum.ResStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class RestClient {
    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private HttpStatus status;

    public RestClient() {
        this.restTemplate = new RestTemplate();
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }

    public String post(String uri, String json, Map<String, Object> headersMap) {
        log.debug("-----------------------------------------------------------------");
        log.debug("Proxy Post   \t=> [{}] ", uri);
        log.debug("Proxy Param \t=> {}", json);

        for (Map.Entry<String, Object> entry : headersMap.entrySet()) {
            headers.add(entry.getKey(), entry.getValue().toString());
        }

        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            // 4xx 에러 처리
            e.printStackTrace();
            log.error("Client error: {}", e.getStatusCode());
            return ResStatus.HTTP_STATUS_4XX.getSCode();
        } catch (HttpServerErrorException e) {
            // 5xx 에러 처리
            e.printStackTrace();
            log.error("Server error: {}", e.getStatusCode());
            return ResStatus.HTTP_STATUS_5XX.getSCode();
        }
    }
}
