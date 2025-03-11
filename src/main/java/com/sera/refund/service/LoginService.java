package com.sera.refund.service;

import com.sera.refund.common.TokenProvider;
import com.sera.refund.controller.dto.UserLoginRequest;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public String login(UserLoginRequest request) {
        Authentication user = authenticate(request);
        return tokenProvider.generateToken(user.getName());
    }

    private Authentication authenticate(UserLoginRequest request) {
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        if (!authentication.isAuthenticated()) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD, "Invalid user ID or password");
        }
        return authentication;
    }

}
