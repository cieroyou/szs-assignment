package com.sera.refund.service;

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
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String encodedRegistrationNo = passwordEncoder.encode(request.getRegNo());
        User user = User.of(request.getUserId(), encodedPassword, request.getName(), encodedRegistrationNo);
        userRepository.save(user);
    }
}
