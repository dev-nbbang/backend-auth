package com.dev.nbbang.auth.global.exception;

import org.springframework.http.HttpStatus;

public abstract class NbbangCommonException extends RuntimeException{
    public NbbangCommonException(String message) {
        super(message);
    }

    public abstract String getErrorCode();      // Enum 타입에 지정한 에러코드
    public abstract HttpStatus getHttpStatus(); // HttpStatus Code
    public abstract String getMessage();        // 실제 호출 메세지
}
