package com.sera.refund.infrastructure.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScrapingResponse {
    private String status;
    private ScrapingData data;
    private ScrapingError errors;

    @Getter
    @Setter
    @ToString
    public static class ScrapingData {
        @JsonProperty("종합소득금액")
        private int totalIncome;

        @JsonProperty("이름")
        private String name;

        @JsonProperty("소득공제")
        private IncomeDeduction incomeDeduction;
    }

    @Getter
    @Setter
    @ToString
    public static class IncomeDeduction {
        @JsonProperty("국민연금")
        private List<PensionDeduction> pensionDeductions;

        @JsonProperty("신용카드소득공제")
        private CreditCardDeduction creditCardDeduction;

        @JsonProperty("세액공제")
        private String taxDeduction;
    }

    // 국민연금 Item
    @Getter
    @Setter
    @ToString
    public static class PensionDeduction {
        @JsonProperty("월")
        private String month;

        @JsonProperty("공제액")
        private String deductionAmount;
    }

    // 신용카드소등공제
    @Getter
    @Setter
    @ToString
    public static class CreditCardDeduction {
        @JsonProperty("month")
        private List<Map<String, String>> monthlyDeductions;

        @JsonProperty("year")
        private int year;
    }

    @Getter
    @Setter
    @ToString
    public static class ScrapingError {
        private String code;
        private String message;
        private String validations;
    }
}

