package com.dev.nbbang.auth.global.exception;


public class ExpiredRefreshTokenException extends RuntimeException {
    private final NbbangException nbbangException;

    public ExpiredRefreshTokenException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public ExpiredRefreshTokenException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }
}
