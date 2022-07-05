package com.dev.nbbang.auth.global.exception;


import org.springframework.http.HttpStatus;

public class ExpiredRefreshTokenException extends NbbangCommonException {
    private final String message;
    private final NbbangException nbbangException;

    public ExpiredRefreshTokenException(String message, NbbangException nbbangException) {
        super(message);
        this.message = message;
        this.nbbangException = nbbangException;
    }

    @Override
    public String getErrorCode() {
        return nbbangException.getCode();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getMessage() {
        return message;
    }
}


