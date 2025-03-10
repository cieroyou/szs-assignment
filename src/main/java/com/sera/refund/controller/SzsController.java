package com.sera.refund.controller;


import com.sera.refund.service.LoginService;
import com.sera.refund.service.ScrapingService;
import com.sera.refund.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/szs")
@RequiredArgsConstructor
public class SzsController {

    private final SignupService signupService;
    private final LoginService loginService;
    private final ScrapingService scrapingService;

    @PostMapping("/signup")
    public String signup(@Valid @RequestBody UserSignupRequest request) {
        signupService.registerUser(request);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserLoginRequest request) {
        String token = loginService.login(request);
        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @PostMapping("/scrap")
    public void scrap(@AuthenticationPrincipal String userId) {
        // 스크래핑 서비스 호출
        scrapingService.scrapData(userId);
    }

}
