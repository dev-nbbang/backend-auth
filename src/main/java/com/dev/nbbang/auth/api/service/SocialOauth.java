package com.dev.nbbang.auth.api.service;

import com.dev.nbbang.auth.api.entity.SocialLoginType;

public interface SocialOauth {
    String requestAccessToken(String code);

    String requestUserInfo(String code);

    default SocialLoginType type() {
        if(this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        }
        else if(this instanceof KakaoOauth){
            return SocialLoginType.KAKAO;
        }
        return null;
    }
}
