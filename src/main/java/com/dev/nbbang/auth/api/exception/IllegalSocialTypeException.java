package com.dev.nbbang.auth.api.exception;


import com.dev.nbbang.auth.global.exception.NbbangException;

public class IllegalSocialTypeException extends RuntimeException {
    private final NbbangException nbbangException;

    public IllegalSocialTypeException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public IllegalSocialTypeException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }

}

