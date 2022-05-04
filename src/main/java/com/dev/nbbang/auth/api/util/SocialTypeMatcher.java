package com.dev.nbbang.auth.api.util;

import com.dev.nbbang.auth.api.entity.SocialLoginType;
import com.dev.nbbang.auth.api.exception.IllegalSocialTypeException;
import com.dev.nbbang.auth.api.service.SocialOauth;
import com.dev.nbbang.auth.global.exception.NbbangException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SocialTypeMatcher {

    private final List<SocialOauth> socialOauthList;
    private final List<SocialAuthUrl> socialAuthUrlList;

    // 소셜 로그인 타입 확인
    public SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(socialOauth -> socialOauth.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalSocialTypeException("잘못된 소셜 로그인 타입입니다.", NbbangException.ILLEGAL_SOCIAL_TYPE));
    }


    public SocialAuthUrl findSocialAuthUrlByType(SocialLoginType socialLoginType) {
        return socialAuthUrlList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalSocialTypeException("잘못된 소셜 로그인 타입입니다.", NbbangException.ILLEGAL_SOCIAL_TYPE));
    }
}
