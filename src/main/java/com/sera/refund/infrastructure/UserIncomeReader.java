package com.sera.refund.infrastructure;

import com.sera.refund.domain.UserIncome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserIncomeReader {

    private final UserIncomeRepository userIncomeRepository;

    @Cacheable(value = "scrapingCache", key = "#userId", unless = "#result == null or #result.isEmpty()")
    public List<UserIncome> readUserIncome(String userId) {
        log.info("🔍 캐시 확인: userId={} 캐시에서 조회 시도", userId);
        List<UserIncome> incomes = userIncomeRepository.findByUserId(userId); // 캐시에 없으면 DB 조회
        log.info("📌 DB 조회 수행됨: userId={}, 결과: {}", userId, incomes.isEmpty() ? "데이터 없음" : "데이터 있음");
        return incomes;
    }
}
