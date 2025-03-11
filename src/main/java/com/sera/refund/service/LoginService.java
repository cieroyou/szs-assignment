package com.sera.refund.service;

import com.sera.refund.common.TokenProvider;
import com.sera.refund.controller.dto.UserLoginRequest;
import com.sera.refund.domain.User;
import com.sera.refund.infrastructure.UserRepository;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public String login(UserLoginRequest request) {
        User user = authenticate(request);
        return tokenProvider.generateToken(user.getUserId());
    }

    // 유저 검증(todo: using UsernamePasswordAuthenticationToken으로 유지보수성 높이기)
    private User authenticate(UserLoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }
        return user;
    }

}
