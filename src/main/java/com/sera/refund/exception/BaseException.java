package com.sera.refund.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    protected ErrorCode errorCode;
    protected String message;

    public BaseException() {
        super();
    }

    public BaseException(ErrorCode code) {
        super(code.getErrorMessage());
        this.errorCode = code;
        this.message = code.getErrorMessage();
    }

    public BaseException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
        this.message = message;
    }

}
