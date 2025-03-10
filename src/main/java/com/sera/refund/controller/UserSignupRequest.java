package com.sera.refund.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "주민등록번호는 필수 입력 항목입니다.")
    @Pattern(
            regexp = "^\\d{6}-\\d{7}$",
            message = "주민등록번호는 YYMMDD-gabcdef 형식이어야 합니다."
    )
    private String regNo;
}
