package com.sera.refund.service;

import com.sera.refund.common.AesEncryptor;
import com.sera.refund.domain.User;
import com.sera.refund.domain.UserIncome;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import com.sera.refund.infrastructure.ScrapingApiCaller;
import com.sera.refund.infrastructure.UserIncomeReader;
import com.sera.refund.infrastructure.UserIncomeRepository;
import com.sera.refund.infrastructure.UserRepository;
import com.sera.refund.infrastructure.dto.ScrapingData;
import com.sera.refund.infrastructure.dto.ScrapingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapingService {
    private final UserRepository userRepository;
    private final UserIncomeRepository userIncomeRepository;
    private final UserIncomeReader userIncomeReader;
    private final ScrapingApiCaller scrapingApiCaller;

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.COMMON_ENTITY_NOT_FOUND, "User with ID " + userId + " not found"));
    }

    public void scrapData(String userId) {
        List<UserIncome> cachedData = userIncomeReader.readUserIncome(userId);
        // 1. 캐시 조회
        if (!cachedData.isEmpty()) {
            log.info("캐시에 데이터 존재, API 호출 생략 userId={}", userId);
            return;
        }

        User user = getUser(userId);
        // 주민등록번호 복호화
        String decryptedRegNo = AesEncryptor.decrypt(user.getRegNo());

        // 2. 외부 API 호출
        ScrapingData responseData = scrapingApiCaller.callScrapingApi(new ScrapingRequest(user.getName(), decryptedRegNo));

        // 3. 연말정산 데이터 저장
        UserIncome userIncome = responseData.toUserIncome(userId);
        userIncomeRepository.save(userIncome);
        userIncomeRepository.findByUserId(userId); // ✅ 최신 데이터 조회 후 캐시에 저장
    }

    @Async
    public CompletableFuture<Void> scrapDataAsync(String userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                scrapData(userId);
            } catch (Exception e) {
                log.error("스크래핑 비동기 작업 실패: {}", e.getMessage());
            }
        });
    }

}
