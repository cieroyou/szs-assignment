package com.sera.refund.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.sera.refund.common.NumberFormatUtil.formatThousandSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberFormatUtilTest {

    @DisplayName("1,000 단위 구분 기호 테스트")
    @Test
    void testFormatThousandSeparator_PositiveNumber() {
        BigDecimal value = BigDecimal.valueOf(1_000_000);
        String formatted = formatThousandSeparator(value);
        assertEquals("1,000,000", formatted);
    }


    @DisplayName("음수 숫자의 1,000 단위 구분 기호 테스트")
    @Test
    void testFormatThousandSeparator_NegativeNumber() {
        BigDecimal value = BigDecimal.valueOf(-9876543);
        String formatted = formatThousandSeparator(value);
        assertEquals("-9,876,543", formatted);
    }

    @DisplayName("0의 1,000 단위 구분 기호 테스트")
    @Test
    void testFormatThousandSeparator_Zero() {
        BigDecimal value = BigDecimal.ZERO;
        String formatted = formatThousandSeparator(value);
        assertEquals("0", formatted);
    }

    @DisplayName("null 입력 시 기본값 처리 테스트")
    @Test
    void testFormatThousandSeparator_NullValue() {
        String formatted = formatThousandSeparator(null);
        assertEquals("0", formatted);
    }
}