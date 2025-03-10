package com.sera.refund.service;

import com.sera.refund.controller.UserSignupRequest;
import com.sera.refund.domain.User;
import com.sera.refund.domain.UserRepository;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {
    @InjectMocks
    private SignupService signupService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    private User 관우;

    @BeforeEach
    void setUp() {
        관우 = User.of("testUser", "encodedPassword", "관우", "681108-1582816");
    }

    @Test
    void 회원가입_성공() {
        // given
        UserSignupRequest request = new UserSignupRequest("kw1", "password", "관우", "681108-1582816");
        when(userRepository.findByUserId(request.getUserId())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(passwordEncoder.encode(request.getRegNo())).thenReturn("encodedRegNo");

        // when
        signupService.registerUser(request);

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void 회원가입_실패_중복_아이디() {
        // given
        UserSignupRequest request = new UserSignupRequest("kw1", "password", "관우", "681108-1582816");
        when(userRepository.findByUserId(request.getUserId())).thenReturn(Optional.of(관우));

        // when
        // then
        BaseException exception = assertThrows(BaseException.class, () -> signupService.registerUser(request));
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void 회원가입_실패_허용되지_않은_사용자() {
        // given
        UserSignupRequest request = new UserSignupRequest("kw1", "password", "세라", "681108-1582816");
        when(userRepository.findByUserId(request.getUserId())).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
//        when(passwordEncoder.encode(request.getRegNo())).thenReturn("encodedRegNo");

        // when
        // then
        BaseException exception = assertThrows(BaseException.class, () -> signupService.registerUser(request));
        assertEquals(ErrorCode.NOT_ALLOWED_USER, exception.getErrorCode());
    }
}