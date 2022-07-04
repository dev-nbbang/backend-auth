package com.dev.nbbang.auth.api.exception;


import com.dev.nbbang.auth.global.exception.NbbangException;

public class FailGenerateTokenException extends RuntimeException {
    private final NbbangException nbbangException;

    public FailGenerateTokenException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public FailGenerateTokenException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

