package com.sera.refund.infrastructure;

import com.sera.refund.infrastructure.dto.ScrapingData;
import com.sera.refund.infrastructure.dto.ScrapingRequest;
import com.sera.refund.infrastructure.dto.ScrapingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScrapingHttpApiCaller implements ScrapingApiCaller {
    private final RestTemplate restTemplate;
    private final String scrapingApiUrl = "https://codetest-v4.3o3.co.kr/scrap";
    private final String X_API_KEY = "X-API-KEY";
    private final String X_API_KEY_VALUE = "eUdJijcuJgmN/xtBKyK2bg==";

    @Override
    public ScrapingData callScrapingApi(ScrapingRequest request) {
        // todo: 요청 에러처리 (empty secret, 등등)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(X_API_KEY, X_API_KEY_VALUE);
        HttpEntity<ScrapingRequest> requestEntity = new HttpEntity<>(request, headers);

        // ScrapingResponse DTO로 매핑하여 JSON 변환 처리
        ScrapingResponse response = null;
        try {
            response = restTemplate.postForObject(scrapingApiUrl, requestEntity, ScrapingResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("스크래핑 API 호출 실패");
        }
        log.info("response: {}", response.toString());
        assert response != null;
        return response.getData();
    }
}
