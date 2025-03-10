package com.sera.refund.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberFormatUtil {
    public static String formatThousandSeparator(BigDecimal amount) {
        if(amount == null) {
            amount = BigDecimal.ZERO;
        }
        NumberFormat formatter = NumberFormat.getInstance(Locale.KOREA);
        return formatter.format(amount);
    }
}
