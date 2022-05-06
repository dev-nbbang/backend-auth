package com.dev.nbbang.auth.authentication.exception;


import com.dev.nbbang.auth.global.exception.NbbangException;

public class DuplicateMemberIdException extends RuntimeException {
    private final NbbangException nbbangException;

    public DuplicateMemberIdException(String message, NbbangException nbbangException) {
        super(message);
        this.nbbangException = nbbangException;
    }

    public DuplicateMemberIdException(NbbangException nbbangException) {
        super(nbbangException.getMessage());
        this.nbbangException = nbbangException;
    }

    public NbbangException getNbbangException() {
        return this.nbbangException;
    }
}
