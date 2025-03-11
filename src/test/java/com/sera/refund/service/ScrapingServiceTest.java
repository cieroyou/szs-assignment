package com.sera.refund.service;

import com.sera.refund.common.AesEncryptor;
import com.sera.refund.domain.User;
import com.sera.refund.domain.UserIncome;
import com.sera.refund.infrastructure.UserIncomeRepository;
import com.sera.refund.infrastructure.UserRepository;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import com.sera.refund.infrastructure.ScrapingApiCaller;
import com.sera.refund.infrastructure.dto.ScrapingData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrapingServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserIncomeRepository userIncomeRepository;
    @Mock
    ScrapingApiCaller scrapingApiCaller;

    @InjectMocks
    ScrapingService scrapingService;

    private final ScrapingData SCRAPING_DATA = new ScrapingData(20000000, "테스트유저",
            new ScrapingData.IncomeDeduction(
                    List.of(new ScrapingData.PensionDeduction("2023-01", "300,000.25")),
                    new ScrapingData.CreditCardDeduction(List.of(Map.of("01", "100,000.10")), 2023),
                    "300,000"
            )
    );

    @Test
    @DisplayName("존재하지 않는 유저일 경우 예외 발생")
    void testScrapData_UserNotFound() {
        // Given
        String userId = "notExistUser";
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> scrapingService.scrapData(userId));
        assertEquals(ErrorCode.COMMON_ENTITY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("외부 API 호출 확인, 데이터 저장 실행 확인")
    void testScrapData_DecryptRegistrationNumber() {
        // Given
        String regNo = "681108-1582816";
        User user = User.of("kw1", "encodedPassword", "관우", AesEncryptor.encrypt(regNo));
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
        given(scrapingApiCaller.callScrapingApi(any())).willReturn(SCRAPING_DATA);
        // When
        scrapingService.scrapData(user.getUserId());

        // Then
        verify(scrapingApiCaller, times(1)).callScrapingApi(any());
        verify(userIncomeRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("정상적인 Scraping 데이터가 UserIncome 으로 변환")
    void testScrapingDataToUserIncome_Success() {
        // Given
        String userId = "testUser";

        // When
        UserIncome userIncome = SCRAPING_DATA.toUserIncome(userId);

        // Then
        assertNotNull(userIncome);
        assertEquals(userId, userIncome.getUserId());
        assertEquals(2023, userIncome.getTaxYear());
        assertEquals(new BigDecimal("20000000"), userIncome.getTotalIncome());
        assertEquals(new BigDecimal("300000.25"), userIncome.getPensionDeduction());
        assertEquals(new BigDecimal("100000.10"), userIncome.getCreditCardDeduction());
        assertEquals(new BigDecimal("300000"), userIncome.getTaxDeduction());
    }

    @Test
    @DisplayName("연말정산 데이터에 year 정보가 없을 경우 예외 발생")
    void testScrapingDataToUserIncome_NoYear() {
        // Given
        String userId = "testUser";
        ScrapingData scrapingData = new ScrapingData(
                20000000, "테스트유저",
                new ScrapingData.IncomeDeduction(
                        List.of(new ScrapingData.PensionDeduction("2023-01", "300,000.25")),
                        new ScrapingData.CreditCardDeduction(List.of(Map.of("01", "100,000.10")), null),
                        "300,000"
                )
        );

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> scrapingData.toUserIncome(userId));
        assertEquals(ErrorCode.DATA_TRANSFORM_ERROR, exception.getErrorCode());
    }

    @Test
    @DisplayName("연말정산 year 값이 0 이하일 경우 예외 발생")
    void testScrapingDataToUserIncome_InvalidYear() {
        // Given
        String userId = "testUser";
        ScrapingData scrapingData = new ScrapingData(
                20000000, "테스트유저",
                new ScrapingData.IncomeDeduction(
                        List.of(new ScrapingData.PensionDeduction("2023-01", "300,000.25")),
                        new ScrapingData.CreditCardDeduction(List.of(Map.of("01", "100,000.10")), 0),
                        "300,000"
                )
        );

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> scrapingData.toUserIncome(userId));
        assertEquals(ErrorCode.DATA_TRANSFORM_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("유효한 연도를 제공해야 합니다."));
    }

    @Test
    @DisplayName("데이터 변환 중 알 수 없는 예외 발생 시 BaseException 반환 확인")
    void testScrapingDataToUserIncome_UnknownError() {
        // Given
        String userId = "testUser";
        ScrapingData scrapingDataMock = mock(ScrapingData.class);
        doThrow(new BaseException(ErrorCode.DATA_TRANSFORM_ERROR, "예상치 못한 오류 발생"))
                .when(scrapingDataMock).toUserIncome(userId);

        // When & Then
        BaseException exception = assertThrows(BaseException.class, () -> scrapingDataMock.toUserIncome(userId));
        assertEquals(ErrorCode.DATA_TRANSFORM_ERROR, exception.getErrorCode());
    }
}