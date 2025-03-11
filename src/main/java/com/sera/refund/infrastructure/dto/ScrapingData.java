package com.sera.refund.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sera.refund.common.NumberFormatUtil;
import com.sera.refund.domain.UserIncome;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.sera.refund.common.NumberFormatUtil.safeBigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScrapingData {
    @JsonProperty("종합소득금액")
    private int totalIncome;

    @JsonProperty("이름")
    private String name;

    @JsonProperty("소득공제")
    private IncomeDeduction incomeDeduction;

    public UserIncome toUserIncome(String userId) {
        try {
            if (this.incomeDeduction == null || this.incomeDeduction.getCreditCardDeduction() == null) {
                throw new IllegalArgumentException("연말정산 데이터에 year 정보가 없습니다.");
            }

            int year = this.incomeDeduction.getCreditCardDeduction().getYear();
            if (year <= 0) {
                throw new IllegalArgumentException("유효한 연도를 제공해야 합니다.");
            }

            // 국민연금 공제 합산
            BigDecimal pensionDeduction = this.incomeDeduction.getPensionDeductions() != null
                    ? this.incomeDeduction.getPensionDeductions().stream()
                    .map(p -> safeBigDecimal(p.getDeductionAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO;

            // 신용카드 소득공제 합산
            BigDecimal creditCardDeduction = this.incomeDeduction.getCreditCardDeduction().getMonthlyDeductions() != null
                    ? this.incomeDeduction.getCreditCardDeduction().getMonthlyDeductions().stream()
                    .flatMap(m -> m.values().stream())
                    .map(NumberFormatUtil::safeBigDecimal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO;

            // 세액공제
            BigDecimal taxDeduction = safeBigDecimal(this.incomeDeduction.getTaxDeduction());

            return UserIncome.builder()
                    .userId(userId)
                    .year(year)
                    .totalIncome(new BigDecimal(this.totalIncome))
                    .pensionDeduction(pensionDeduction)
                    .creditCardDeduction(creditCardDeduction)
                    .taxDeduction(taxDeduction)
                    .build();

        } catch (IllegalArgumentException e) {
            throw new BaseException(ErrorCode.DATA_TRANSFORM_ERROR, String.format("UserIncome 변환 실패 - userId: %s, 이유: %s", userId, e.getMessage()));
        } catch (Exception e) {
            throw new BaseException(ErrorCode.DATA_TRANSFORM_ERROR, String.format("UserIncome 변환 중 알 수 없는 오류 발생 - userId: %s", userId));
        }

    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
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
    @AllArgsConstructor
    @NoArgsConstructor
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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreditCardDeduction {
        @JsonProperty("month")
        private List<Map<String, String>> monthlyDeductions;

        @JsonProperty("year")
        private Integer year;
    }
}

