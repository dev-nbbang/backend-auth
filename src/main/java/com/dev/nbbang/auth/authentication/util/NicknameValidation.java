package com.dev.nbbang.auth.authentication.util;

import com.dev.nbbang.auth.authentication.exception.IllegalNicknameException;
import com.dev.nbbang.auth.global.exception.NbbangException;

import java.util.regex.Pattern;

public class NicknameValidation {
    public static Boolean valid(String nickname) {
        // 특수문자 및 공백 제외
        return Pattern.compile("[^a-zA-Z0-9ㄱ-힣]").matcher(nickname).find() || nickname.length() < 3 || nickname.length() > 10;
    }
}
