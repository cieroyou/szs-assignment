package com.sera.refund.service;

import com.sera.refund.controller.dto.UserRefundResponse;
import com.sera.refund.domain.TaxCalculator;
import com.sera.refund.domain.UserIncome;
import com.sera.refund.domain.UserIncomeRepository;
import com.sera.refund.domain.UserRepository;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.sera.refund.common.NumberFormatUtil.formatThousandSeparator;

@Service
@RequiredArgsConstructor
public class RefundService {
    private final UserIncomeRepository userIncomeRepository;
    private final UserRepository userRepository;
    private final TaxCalculator taxCalculator;

    @Transactional(readOnly = true)
    public List<UserRefundResponse> calculateRefund(String userId) {
        validateUser(userId);
        List<UserIncome> incomes = userIncomeRepository.findByUserId(userId);

        return incomes.stream().map(income -> {
            // 1. 과세표준 계산 (종합소득금액 - 공제액), 소수점 공제액으로 인한 소수점 발생시 반올림 적용
            BigDecimal taxableIncome = income.getTaxableIncome().setScale(0, RoundingMode.HALF_UP);
            // 2. 산출세액 계산
            BigDecimal calculatedTax = taxCalculator.calculateTax(taxableIncome);
            // 3. 결정세액 계산 (산출세액 - 세액공제)
            BigDecimal finalTax = calculatedTax.subtract(income.getTaxDeduction())
                    .setScale(0, RoundingMode.HALF_UP);

            // 천단위 구분 적용
            String formattedFinalTax = formatThousandSeparator(finalTax);
            return new UserRefundResponse(income.getTaxYear(), formattedFinalTax);

        }).toList();

    }

    private void validateUser(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new BaseException(ErrorCode.COMMON_ENTITY_NOT_FOUND, "User with ID " + userId + " not found");
        }
    }


}
