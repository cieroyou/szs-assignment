package com.sera.refund.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberFormatUtil {
    public static String formatThousandSeparator(BigDecimal amount) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        NumberFormat formatter = NumberFormat.getInstance(Locale.KOREA);
        return formatter.format(amount);
    }


    /**
     * 원 표기가 포함된 문자열을 BigDecimal로 변환
     *
     * @param value 금액 문자열 (예: "300,000.25")
     * @return 변환된 BigDecimal 값
     */
    public static BigDecimal safeBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 쉼표 제거 후 반환
        return new BigDecimal(value.replace(",", ""));
    }
}
