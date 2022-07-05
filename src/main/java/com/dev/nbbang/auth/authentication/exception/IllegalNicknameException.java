package com.dev.nbbang.auth.authentication.exception;


import com.dev.nbbang.auth.global.exception.NbbangCommonException;
import com.dev.nbbang.auth.global.exception.NbbangException;
import org.springframework.http.HttpStatus;

public class IllegalNicknameException extends NbbangCommonException {
    private final String message;
    private final NbbangException nbbangException;

    public IllegalNicknameException(String message, NbbangException nbbangException) {
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
        return HttpStatus.OK;
    }

    @Override
    public String getMessage() {
        return message;
    }
}


