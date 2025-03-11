package com.sera.refund.service;

import com.sera.refund.controller.dto.UserRefundResponse;
import com.sera.refund.domain.UserIncome;
import com.sera.refund.infrastructure.UserIncomeRepository;
import com.sera.refund.infrastructure.UserRepository;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {
    @Mock
    private UserIncomeRepository userIncomeRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private TaxCalculator taxCalculator;

    @InjectMocks
    private RefundService refundService;

    @Test
    @DisplayName("존재하지 않는 사용자 요청 시 예외 발생")
    void testRefundUserNotFound() {
        // Given: 존재하지 않는 사용자
        String userId = "notExistUser";
        given(userRepository.existsByUserId(userId)).willReturn(false);

        // When & Then: 예외 발생 검증
        BaseException exception = assertThrows(BaseException.class, () -> refundService.calculateRefund(userId));
        assertEquals(ErrorCode.COMMON_ENTITY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("스크래핑 데이터가 없는 경우 빈 리스트 반환")
    void testRefundNoData() {
        // Given: 정상 사용자지만 연말정산 데이터 없음
        String userId = "testUser";
        given(userRepository.existsByUserId(userId)).willReturn(true);
        given(userIncomeRepository.findByUserId(userId)).willReturn(Collections.emptyList());

        // When
        List<UserRefundResponse> response = refundService.calculateRefund(userId);

        // Then
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("과세표준이 1,400만 원 이하일 때 6% 적용, 천단위 구분 확인, 2023 연도 확인")
    void testLowestTaxBracket() {
        // Given
        String userId = "testUser";
        UserIncome income = new UserIncome(userId, 2023,
                new BigDecimal("14000000"), // ✅ 과세표준 1,400만 원 이하
                BigDecimal.ZERO,  // 국민연금 공제 없음
                BigDecimal.ZERO,  // 신용카드 공제 없음
                BigDecimal.ZERO); // 세액공제 없음
        given(userRepository.existsByUserId(userId)).willReturn(true);
        given(userIncomeRepository.findByUserId(userId)).willReturn(List.of(income));
        given(taxCalculator.calculateTax(any())).willReturn(new BigDecimal("840000")); // ✅ 1,400만 원 * 6%

        // When
        List<UserRefundResponse> response = refundService.calculateRefund(userId);

        // Then
        assertEquals("840,000", response.get(0).getRefund());
        assertEquals(2023, response.get(0).getYear());
    }


    // 과세표준이 1,400만 원 이하일 때, 국민연금, 신용카드, 공제 값 적용되어 결정세액 - 세액공제 값 확인
    @Test
    @DisplayName("과세표준이 1,400만 원 이하일 때, 세액공제 값 적용되어 결정세액 - 세액공제 값 확인")
    void testLowestTaxBracketWithDeductions() {
        // Given
        String userId = "testUser";
        UserIncome income = new UserIncome(userId, 2023,
                new BigDecimal("14000000"), // ✅ 과세표준 1,400만 원 이하
                new BigDecimal("0"),  // 국민연금 공제
                new BigDecimal("0"),  // 신용카드 공제
                new BigDecimal("840000"));  // 세액공제
        given(userRepository.existsByUserId(userId)).willReturn(true);
        given(userIncomeRepository.findByUserId(userId)).willReturn(List.of(income));
        given(taxCalculator.calculateTax(any())).willReturn(new BigDecimal("840000")); // ✅ 1,400만 원 * 6%

        // When
        List<UserRefundResponse> response = refundService.calculateRefund(userId);

        // Then: 결정세액 - 세액공제 값 확인
        assertEquals("0", response.get(0).getRefund());
        assertEquals(2023, response.get(0).getYear());
    }

    @Test
    @DisplayName("과세표준이 1,400만 원 이하일 때, 결정세액 (-)값 반환")
    void testLowestTaxBracketWithNegativeValue() {
        // Given
        String userId = "testUser";
        UserIncome income = new UserIncome(userId, 2023,
                new BigDecimal("14000000"), // ✅ 과세표준 1,400만 원 이하
                BigDecimal.ZERO,  // 국민연금 공제 없음
                BigDecimal.ZERO,  // 신용카드 공제 없음
                new BigDecimal("1000000")); // 세액공제
        given(userRepository.existsByUserId(userId)).willReturn(true);
        given(userIncomeRepository.findByUserId(userId)).willReturn(List.of(income));
        given(taxCalculator.calculateTax(any())).willReturn(new BigDecimal("840000")); // ✅ 1,400만 원 * 6%

        // When
        List<UserRefundResponse> response = refundService.calculateRefund(userId);

        // Then
        assertEquals("-160,000", response.get(0).getRefund());
        assertEquals(2023, response.get(0).getYear());
    }
}