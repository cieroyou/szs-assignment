package com.sera.refund.service;

import com.sera.refund.common.AesEncryptor;
import com.sera.refund.domain.User;
import com.sera.refund.domain.UserIncome;
import com.sera.refund.domain.UserIncomeRepository;
import com.sera.refund.domain.UserRepository;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import com.sera.refund.infrastructure.ScrapingApiCaller;
import com.sera.refund.infrastructure.dto.ScrapingData;
import com.sera.refund.infrastructure.dto.ScrapingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class ScrapingService {
    private final UserRepository userRepository;
    private final UserIncomeRepository userIncomeRepository;
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

        // 2. 외부 API 호출
        ScrapingData responseData = scrapingApiCaller.callScrapingApi(new ScrapingRequest(user.getName(), decryptedRegNo));

        // 3. 연말정산 데이터 저장

        // todo: year 이 없으면 에러처리
        saveUserIncome(userId, responseData);
    }

    //  안전한 BigDecimal 변환 함수 (쉼표 제거 & null 값 처리)
    private BigDecimal safeBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.replace(",", ""));
    }


    private void saveUserIncome(String userId, ScrapingData data) {
        // ✅ year 값 검증
        if (data.getIncomeDeduction() == null || data.getIncomeDeduction().getCreditCardDeduction() == null) {
            throw new IllegalArgumentException("연말정산 데이터에 year 정보가 없습니다.");
        }

        int year = data.getIncomeDeduction().getCreditCardDeduction().getYear();
        if (year <= 0) {
            throw new IllegalArgumentException("유효한 연도를 제공해야 합니다.");
        }

        // todo: 객체 변환 로직을 ScrapingData.toUserIncome(userId)에 캡슐화하는 것이 유지보수성 측면에서 좋을 수 있음
        BigDecimal totalIncome = new BigDecimal(data.getTotalIncome()); // ✅ 종합소득금액

        // 국민연금 공제 합산
        BigDecimal pensionDeduction = data.getIncomeDeduction().getPensionDeductions() != null
                ? data.getIncomeDeduction().getPensionDeductions().stream()
                .map(p -> safeBigDecimal(p.getDeductionAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;
        // 신용카드 소득공제 합산
        BigDecimal creditCardDeduction = data.getIncomeDeduction().getCreditCardDeduction().getMonthlyDeductions() != null
                ? data.getIncomeDeduction().getCreditCardDeduction().getMonthlyDeductions().stream()
                .flatMap(m -> m.values().stream())
                .map(this::safeBigDecimal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        // 세액공제
        BigDecimal taxDeduction = safeBigDecimal(data.getIncomeDeduction().getTaxDeduction());

        UserIncome userIncome = UserIncome.builder()
                .userId(userId)
                .year(year)
                .totalIncome(totalIncome)
                .pensionDeduction(pensionDeduction)
                .creditCardDeduction(creditCardDeduction)
                .taxDeduction(taxDeduction)
                .build();

        userIncomeRepository.save(userIncome);
    }

}
