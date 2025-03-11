package com.sera.refund.controller;


import com.sera.refund.common.response.CommonResponse;
import com.sera.refund.controller.dto.UserLoginRequest;
import com.sera.refund.controller.dto.UserLoginResponse;
import com.sera.refund.controller.dto.UserRefundResponse;
import com.sera.refund.controller.dto.UserSignupRequest;
import com.sera.refund.service.LoginService;
import com.sera.refund.service.RefundService;
import com.sera.refund.service.ScrapingService;
import com.sera.refund.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/szs")
@RequiredArgsConstructor
public class SzsController {

    private final SignupService signupService;
    private final LoginService loginService;
    private final ScrapingService scrapingService;
    private final RefundService refundService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signup(@Valid @RequestBody UserSignupRequest request) {
        signupService.registerUser(request);
    }

    @PostMapping("/login")
    public CommonResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        String token = loginService.login(request);
        return CommonResponse.success(new UserLoginResponse(token));
    }

    @PostMapping("/scrap")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void scrap(@AuthenticationPrincipal String userId) {
        scrapingService.scrapData(userId);
    }

    @GetMapping("/refund")
    public CommonResponse<List<UserRefundResponse>> refund(@AuthenticationPrincipal String userId) {
        return CommonResponse.success(refundService.calculateRefund(userId));
    }
}
