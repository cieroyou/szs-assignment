package com.sera.refund.service;

import com.sera.refund.common.AllowedUsers;
import com.sera.refund.controller.UserSignupRequest;
import com.sera.refund.domain.User;
import com.sera.refund.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void registerUser(UserSignupRequest request) {
        // todo: 이미 존재하는지 체크

        // 허용된 사용자만 가입하도록 함
        if (!AllowedUsers.isAllowedUser(request.getName(), request.getRegNo())) {
            // TODO: 예외를 Class 로 정의하여 유지보수성 높일 것
            throw new IllegalArgumentException("허용되지 않은 사용자입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String encodedRegistrationNo = passwordEncoder.encode(request.getRegNo());
        User user = User.of(request.getUserId(), encodedPassword, request.getName(), encodedRegistrationNo);
        userRepository.save(user);
    }
}
