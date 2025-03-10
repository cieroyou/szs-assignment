package com.sera.refund.domain;

import com.sera.refund.configuration.TaxRateConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class TaxCalculator {

    private final TaxRateConfig taxRateConfig;

    public BigDecimal calculateTax(BigDecimal taxableIncome) {
        for (TaxRateConfig.TaxBracket bracket : taxRateConfig.getBrackets()) {
            if (taxableIncome.compareTo(bracket.getLowerBound()) >= 0 &&
                    taxableIncome.compareTo(bracket.getUpperBound()) <= 0) {
                return calculateTax(taxableIncome, bracket.getLowerBound(), bracket.getTaxRate(), bracket.getProgressiveDeduction());
            }
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal calculateTax(BigDecimal taxableIncome, BigDecimal lowerBound, BigDecimal taxRate, BigDecimal progressiveDeduction) {
        // 산출세액 = 누진공제 + (과세표준 - 기준) * 세율
        return taxableIncome.subtract(lowerBound) // 기준값 차감
                .multiply(taxRate) //  세율 적용
                .add(progressiveDeduction) // 누진공제 추가
                .setScale(0, RoundingMode.HALF_UP); // 최종 결과에서 반올림 적용
    }


}
