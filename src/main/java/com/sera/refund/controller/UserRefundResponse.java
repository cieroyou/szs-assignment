package com.sera.refund.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRefundResponse {
    @JsonProperty("년도")
    private int year;

    @JsonProperty("결정세액")
    private String refund;
}
