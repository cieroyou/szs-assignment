package com.sera.refund.controller;


import com.sera.refund.service.LoginService;
import com.sera.refund.service.RefundService;
import com.sera.refund.service.ScrapingService;
import com.sera.refund.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/szs")
@RequiredArgsConstructor
public class SzsController {

    private final SignupService signupService;
    private final LoginService loginService;
    private final ScrapingService scrapingService;
    private final RefundService refundService;

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
        scrapingService.scrapData(userId);
    }

    @GetMapping("/refund")
    public List<UserRefundResponse> refund(@AuthenticationPrincipal String userId) {
        return refundService.calculateRefund(userId);
    }
}
