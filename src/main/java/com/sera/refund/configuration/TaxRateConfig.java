package com.sera.refund.configuration;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Component
@ConfigurationProperties(prefix = "tax")
public class TaxRateConfig {

    private final List<TaxBracket> brackets;

    // 세율 데이터 로드
    public TaxRateConfig(List<TaxBracket> brackets) {
        this.brackets = brackets;
    }

    @Getter
    public static class TaxBracket {
        private final BigDecimal lowerBound;
        private final BigDecimal upperBound;
        private final BigDecimal taxRate;
        private final BigDecimal progressiveDeduction;

        public TaxBracket(BigDecimal lowerBound, BigDecimal upperBound, BigDecimal taxRate, BigDecimal progressiveDeduction) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.taxRate = taxRate;
            this.progressiveDeduction = progressiveDeduction;
        }
    }
}
