package com.sera.refund.infrastructure;

import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
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
    private final String API_URL = "https://codetest-v4.3o3.co.kr/scrap";
    private final String X_API_KEY = "X-API-KEY";
    private final String X_API_KEY_VALUE = "eUdJijcuJgmN/xtBKyK2bg==";

    @Override
    public ScrapingData callScrapingApi(ScrapingRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(X_API_KEY, X_API_KEY_VALUE);
        HttpEntity<ScrapingRequest> requestEntity = new HttpEntity<>(request, headers);

        try {
            // 요청 정보 로그
            log.info("[Scraping API 요청] 요청 데이터: ScrapingRequest(name={}, regNo={})", request.getName(), maskRegNo(request.getRegNo()));
            ScrapingResponse response = restTemplate.postForObject(API_URL, requestEntity, ScrapingResponse.class);

            if (response == null || response.getData() == null) {
                throw new BaseException(ErrorCode.API_CALL_FAILED, "스크래핑 API 응답이 비어 있음");
            }

            // 응답 정보 로그
            log.info("[Scraping API 응답] {}", response);

            return response.getData();
        } catch (Exception e) {
            // 모든 예외를 동일한 ErrorCode로 처리
            log.error("[Scraping API 호출 실패] 예외 발생: {}", e.getMessage(), e);
            throw new BaseException(ErrorCode.API_CALL_FAILED, "스크래핑 API 호출 중 오류 발생");
        }
    }

    /**
     * 주민등록번호 마스킹 처리 (앞자리만 보이고 뒷자리는 숨김)
     * ex) 921108-1582816 → 921108-*******
     */
    private String maskRegNo(String regNo) {
        if (regNo == null || regNo.length() < 8) {
            return "INVALID_REGNO"; // 비정상적인 값 처리
        }
        return regNo.substring(0, 7) + "-*******"; // 앞 7자리 유지, 나머지 마스킹
    }
}

