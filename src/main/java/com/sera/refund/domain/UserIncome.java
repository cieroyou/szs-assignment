package com.sera.refund.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_income")
public class UserIncome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(nullable = false, name = "total_income")
    private int totalIncome; // 종합소득금액

    @Column(nullable = false, name = "pension_deduction")
    private BigDecimal pensionDeduction; // 국민연금 공제 (1년 합산)

    @Column(nullable = false, name = "credit_card_deduction")
    private BigDecimal creditCardDeduction; // 신용카드 소득공제 (1년 합산)

    @Column(nullable = false, name = "tax_deduction")
    private BigDecimal taxDeduction; // 세액공제

    public UserIncome(String userId, int totalIncome, BigDecimal pensionDeduction, BigDecimal creditCardDeduction, BigDecimal taxDeduction) {
        this.userId = userId;
        this.totalIncome = totalIncome;
        this.pensionDeduction = pensionDeduction;
        this.creditCardDeduction = creditCardDeduction;
        this.taxDeduction = taxDeduction;
    }

    // 과세표준 계산 (종합소득금액 - 공제액)
    public BigDecimal getTaxableIncome() {
        return BigDecimal.valueOf(totalIncome)
                .subtract(pensionDeduction)
                .subtract(creditCardDeduction);
    }

}
