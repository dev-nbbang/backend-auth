package com.dev.nbbang.auth.authentication.util;

import com.dev.nbbang.auth.authentication.exception.IllegalNicknameException;
import com.dev.nbbang.auth.global.exception.NbbangException;

public class NicknameValidation {
    public static Boolean valid(String nickname) {
        // 특수문자 및 공백 제외
        if(!nickname.matches("[^\\wㄱ-힣]|[\\_]") || nickname.length() < 3 || nickname.length() > 10) return false;

        return true;
    }
}
