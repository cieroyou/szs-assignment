package com.sera.refund.service;

import com.sera.refund.common.TokenProvider;
import com.sera.refund.controller.UserLoginRequest;
import com.sera.refund.domain.User;
import com.sera.refund.domain.UserRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

}
