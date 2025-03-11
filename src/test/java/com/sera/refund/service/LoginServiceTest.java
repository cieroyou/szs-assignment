package com.sera.refund.service;

import com.sera.refund.common.TokenProvider;
import com.sera.refund.controller.dto.UserLoginRequest;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @InjectMocks
    private LoginService loginService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenProvider tokenProvider;
    private User 관우;

    @BeforeEach
    void setUp() {
        관우 = User.of("kw1", "encodedPassword", "관우", "681108-1582816");
    }


    @Test
    void 로그인_성공() {
        // given
        UserLoginRequest request = new UserLoginRequest("kw1", "password");
        when(userRepository.findByUserId(request.getUserId())).thenReturn(Optional.of(관우));
        when(passwordEncoder.matches(request.getPassword(), 관우.getPassword())).thenReturn(true);
        when(tokenProvider.generateToken(관우.getUserId())).thenReturn("token");

        // when
        String token = loginService.login(request);

        // then
        assertEquals("token", token);
    }

    @Test
    void 로그인_실패_아이디_없음() {
        // given
        UserLoginRequest request = new UserLoginRequest("kw1", "password");
        when(userRepository.findByUserId(request.getUserId())).thenReturn(Optional.empty());

        // when
        // then
        BaseException exception = assertThrows(BaseException.class, () -> loginService.login(request));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 로그인_실패_비밀번호_불일치() {
        // given
        UserLoginRequest request = new UserLoginRequest("kw1", "password");
        when(userRepository.findByUserId(request.getUserId())).thenReturn(Optional.of(관우));
        when(passwordEncoder.matches(request.getPassword(), 관우.getPassword())).thenReturn(false);

        // when
        // then
        BaseException exception = assertThrows(BaseException.class, () -> loginService.login(request));
        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }
}