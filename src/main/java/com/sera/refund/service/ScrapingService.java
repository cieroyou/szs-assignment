package com.sera.refund.service;

import com.sera.refund.common.AesEncryptor;
import com.sera.refund.domain.User;
import com.sera.refund.domain.UserRepository;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import com.sera.refund.infrastructure.ScrapingApiCaller;
import com.sera.refund.infrastructure.dto.ScrapingRequest;
import com.sera.refund.infrastructure.dto.ScrapingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ScrapingService {
    private final UserRepository userRepository;
    private final ScrapingApiCaller scrapingApiCaller;

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    public void scrapData(String userId) {
        User user = getUser(userId);

        // 주민등록번호 복호화
        String decryptedRegNo = AesEncryptor.decrypt(user.getRegNo());

        // 스크래핑 로직
        ScrapingResponse response = scrapingApiCaller.callScrapingApi(new ScrapingRequest(user.getName(), decryptedRegNo));

        // 필요 정보 DB 저장
    }
}
