package com.sera.refund.domain;

import com.sera.refund.configuration.TaxRateConfig;
import com.sera.refund.service.TaxCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TaxCalculatorTest {

    @Autowired
    TaxRateConfig taxRateConfig;

    @Autowired
    private TaxCalculator taxCalculator;

    @DisplayName("과세표준 2,000만원 -> 1,740,000원 = 84만원 + (2,000만원 - 1,400만원) * 15%")
    @Test
    void test() {
        BigDecimal result = TaxCalculator.calculateTax(BigDecimal.valueOf(20000000), BigDecimal.valueOf(14000000), BigDecimal.valueOf(0.15), BigDecimal.valueOf(840000));
        assertEquals(BigDecimal.valueOf(1740000), result);
    }

    @DisplayName("과세표준 10,000,000원 -> 세율 6% 적용")
    @Test
    void testCalculateTax_LowestBracket() {
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(10_000_000));
        assertEquals(BigDecimal.valueOf(600_000), result);
    }

    @DisplayName("9,999,999원(1,400만원 이하) -> 6% 적용 후 반올림")
    @Test
    void testCalculateTax_WithRounding() {
        // 9,999,999원(1,400만원 이하) -> 6% 적용 후 반올림
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(9999999));
        assertEquals(BigDecimal.valueOf(600000), result);// 9999999 * 0.06 = 599999.94 → 반올림하여 600000
    }

    @DisplayName("과세표준 20,000,000원 -> (20,000,000 - 14,000,000) * 15% + 840,000")
    @Test
    void testCalculateTax_SecondBracket() {
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(20_000_000));
        assertEquals(BigDecimal.valueOf(1_740_000), result);
    }

    @DisplayName("과세표준 55,000,000원 -> (55,000,000 - 50,000,000) * 24% + 6,240,000")
    @Test
    void testCalculateTax_ThirdBracket() {
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(55_000_000));
        assertEquals(BigDecimal.valueOf(7_440_000), result);
    }

    @DisplayName("과세표준 120,000,000원 -> (120,000,000 - 88,000,000) * 35% + 15,360,000")
    @Test
    void testCalculateTax_FourthBracket() {
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(120_000_000));
        assertEquals(BigDecimal.valueOf(26_560_000), result);
    }

    @DisplayName("과세표준 200,000,000원 -> (200,000,000 - 150,000,000) * 38% + 19,400,000")
    @Test
    void testCalculateTax_FifthBracket() {
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(200_000_000));
        assertEquals(BigDecimal.valueOf(38_400_000), result);
    }

    @DisplayName("과세표준 400,000,000원 -> (400,000,000 - 300,000,000) * 40% + 25,400,000")
    @Test
    void testCalculateTax_SixthBracket() {
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(400_000_000));
        assertEquals(BigDecimal.valueOf(65_400_000), result);
    }

    @DisplayName("과세표준 800,000,000원 -> (800,000,000 - 500,000,000) * 42% + 37,406,000")
    @Test
    void testCalculateTax_SeventhBracket() {
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(800_000_000));
        assertEquals(BigDecimal.valueOf(163_406_000), result);
    }

    @DisplayName("과세표준 1,500,000,000원 -> (1,500,000,000 - 1,000,000,000) * 45% + 38,406,000")
    @Test
    void testCalculateTax_HighestBracket() {
        BigDecimal result = taxCalculator.calculateTax(BigDecimal.valueOf(1_500_000_000));
        assertEquals(BigDecimal.valueOf(263_406_000), result);
    }
}