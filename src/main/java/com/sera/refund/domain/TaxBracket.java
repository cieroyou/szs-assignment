package com.sera.refund.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class TaxBracket {
    private final BigDecimal lowerBound; // 기준값 (하한값)
    private final BigDecimal upperBound; // 상한값
    private final BigDecimal taxRate; // 세율
    private final BigDecimal progressiveDeduction; // 누진공제
}
