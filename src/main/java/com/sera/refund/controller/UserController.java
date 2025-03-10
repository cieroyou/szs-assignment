package com.sera.refund.controller;


import com.sera.refund.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/szs")
@RequiredArgsConstructor
public class UserController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public String signup(@Valid @RequestBody UserSignupRequest request) {
        signupService.registerUser(request);
        return "회원가입 성공";
    }
}
