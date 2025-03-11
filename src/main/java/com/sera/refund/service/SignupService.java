package com.sera.refund.service;

import com.sera.refund.common.AesEncryptor;
import com.sera.refund.common.AllowedUsers;
import com.sera.refund.controller.dto.UserSignupRequest;
import com.sera.refund.domain.User;
import com.sera.refund.infrastructure.UserRepository;
import com.sera.refund.exception.BaseException;
import com.sera.refund.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserSignupRequest request) {
        // 중복 아이디 체크
        if (userRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new BaseException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 허용된 사용자만 가입하도록 함
        if (!AllowedUsers.isAllowedUser(request.getName(), request.getRegNo())) {
            throw new BaseException(ErrorCode.NOT_ALLOWED_USER);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String encryptedRegistrationNo = AesEncryptor.encrypt(request.getRegNo());
        User user = User.of(request.getUserId(), encodedPassword, request.getName(), encryptedRegistrationNo);
        userRepository.save(user);
    }
}
