package com.dev.nbbang.auth.api.util;


import com.dev.nbbang.auth.api.entity.SocialLoginType;

public interface SocialAuthUrl {
    String makeAuthorizationUrl();

    default SocialLoginType type() {
        if(this instanceof GoogleAuthUrl) {
            return SocialLoginType.GOOGLE;
        } else if(this instanceof  KakaoAuthUrl) {
            return SocialLoginType.KAKAO;
        } else {
            return null;
        }
    }
}