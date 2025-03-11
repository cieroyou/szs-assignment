package com.sera.refund.service;

import com.sera.refund.common.TokenProvider;
import com.sera.refund.controller.dto.UserLoginRequest;
import com.sera.refund.domain.User;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @InjectMocks
    private LoginService loginService;
    @Mock
    TokenProvider tokenProvider;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    private User 관우;

    @BeforeEach
    void setUp() {
        관우 = User.of("kw1", "encodedPassword", "관우", "681108-1582816");
    }


    @Test
    void 로그인_성공() {
        // given
        String token = "mocked-token";
        UserLoginRequest request = new UserLoginRequest("kw1", "password");
        Authentication authToken = new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword());
        Authentication authResult = new UsernamePasswordAuthenticationToken(request.getUserId(), null, Collections.emptyList());

        when(authenticationManager.authenticate(authToken)).thenReturn(authResult);
        when(tokenProvider.generateToken(request.getUserId())).thenReturn(token);

        // when
        String resultToken = loginService.login(request);

        // then
        assertEquals("mocked-token", resultToken);
    }

    @Test
    void 로그인_실패_아이디_없음() {
        // given
        UserLoginRequest request = new UserLoginRequest("kw1", "password");
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword());

        when(authenticationManager.authenticate(token))
                .thenThrow(new BaseException(ErrorCode.USER_NOT_FOUND, "User not found"));

        // when
        // then
        BaseException exception = assertThrows(BaseException.class, () -> loginService.login(request));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
//
    @Test
    void 로그인_실패_비밀번호_불일치() {
        // given
        UserLoginRequest request = new UserLoginRequest("kw1", "password");
        Authentication authToken = new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword());

        when(authenticationManager.authenticate(authToken))
                .thenThrow(new BaseException(ErrorCode.INVALID_PASSWORD, "Invalid user ID or password"));


        // when
        // then
        BaseException exception = assertThrows(BaseException.class, () -> loginService.login(request));
        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
    }
}