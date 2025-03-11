package com.sera.refund.service;

import com.sera.refund.common.AesEncryptor;
import com.sera.refund.domain.User;
import com.sera.refund.domain.UserIncome;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import com.sera.refund.infrastructure.ScrapingApiCaller;
import com.sera.refund.infrastructure.UserIncomeRepository;
import com.sera.refund.infrastructure.UserRepository;
import com.sera.refund.infrastructure.dto.ScrapingData;
import com.sera.refund.infrastructure.dto.ScrapingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapingService {
    private final UserRepository userRepository;
    private final UserIncomeRepository userIncomeRepository;
    private final ScrapingApiCaller scrapingApiCaller;

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.COMMON_ENTITY_NOT_FOUND, "User with ID " + userId + " not found"));
    }

    public void scrapData(String userId) {
        User user = getUser(userId);

        // 주민등록번호 복호화
        String decryptedRegNo = AesEncryptor.decrypt(user.getRegNo());

        // 2. 외부 API 호출
        ScrapingData responseData = scrapingApiCaller.callScrapingApi(new ScrapingRequest(user.getName(), decryptedRegNo));

        // 3. 연말정산 데이터 저장
        UserIncome userIncome = responseData.toUserIncome(userId);
        userIncomeRepository.save(userIncome);
    }

    @Async
    public CompletableFuture<Void> scrapDataAsync(String userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                // ✅ 1. 유저 조회 (DB)
                User user = getUser(userId);

                // ✅ 2. 주민번호 복호화
                String decryptedRegNo = AesEncryptor.decrypt(user.getRegNo());

                // ✅ 3. 외부 API 호출 (최대 20초)
                ScrapingData responseData = scrapingApiCaller.callScrapingApi(new ScrapingRequest(user.getName(), decryptedRegNo));

                // ✅ 4. 연말정산 데이터 저장 (DB, 트랜잭션 적용)
                UserIncome userIncome = responseData.toUserIncome(userId);
                userIncomeRepository.save(userIncome);

            } catch (Exception e) {
                log.error("스크래핑 비동기 작업 실패: {}", e.getMessage());
            }
        });
    }

}
