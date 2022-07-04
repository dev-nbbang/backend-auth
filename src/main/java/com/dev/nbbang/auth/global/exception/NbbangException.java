package com.dev.nbbang.auth.global.exception;

public enum NbbangException {
    NOT_FOUND_MEMBER ("BE001", "No Such a Member"),
    ILLEGAL_SOCIAL_TYPE("BE002", "Input Illegal Social Type"),
    FAIL_TO_CREATE_AUTH_URL("BE003", "Failed To Create Social Auth Url"),
    NO_CREATE_MEMBER("BE004", "Doesn't Create Member"),
    DUPLICATE_MEMBER_ID("BE005", "Duplicate Member Id"),
    EXPIRED_REFRESH_TOKEN("BE006", "Expired Refresh Token"),
    FAIL_GENERATE_TOKEN("BE007", "Fail To Genereate Token");

    private String code;
    private String message;

    NbbangException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
