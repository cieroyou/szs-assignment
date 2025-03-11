package com.sera.refund.service;

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

    // 다른 서비스에서도 사용할 가능성이 있다면 TaxUtil로 분리하는게 좋을거 같음

    /**
     * // 산출세액 = 누진공제 + (과세표준 - 기준) * 세율
     *
     * @param taxableIncome        과세표준
     * @param lowerBound           기준
     * @param taxRate              세율
     * @param progressiveDeduction 누진공제
     * @return 산출세액
     */
    public static BigDecimal calculateTax(BigDecimal taxableIncome, BigDecimal lowerBound, BigDecimal taxRate, BigDecimal progressiveDeduction) {
        return taxableIncome.subtract(lowerBound) // 기준값 차감
                .multiply(taxRate) //  세율 적용
                .add(progressiveDeduction) // 누진공제 추가
                .setScale(0, RoundingMode.HALF_UP); // 최종 결과에서 반올림 적용
    }


}
