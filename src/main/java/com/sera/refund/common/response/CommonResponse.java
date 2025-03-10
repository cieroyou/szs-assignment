package com.sera.refund.common.response;


import com.sera.refund.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse<T> {

    T data;
    String message;
    String errorCode;

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        return CommonResponse.<T>builder()
                .message(errorCode.getErrorMessage())
                .errorCode(errorCode.name())
                .build();
    }

    public static <T> CommonResponse<T> fail(String message, String errorCode) {
        return CommonResponse.<T>builder()
                .message(message)
                .errorCode(errorCode)
                .build();
    }

}
