package com.sera.refund.infrastructure.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScrapingResponse {
    private String status;
    private ScrapingData data;
    private ScrapingError errors;


    @Getter
    @Setter
    @ToString
    public static class ScrapingError {
        private String code;
        private String message;
        private String validations;
    }
}

