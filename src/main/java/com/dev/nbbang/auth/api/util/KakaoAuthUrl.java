package com.dev.nbbang.auth.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RefreshScope
public class KakaoAuthUrl implements SocialAuthUrl {
    @Value("${sns.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${sns.kakao.client-id}")
    private String clientId;

    @Override
    public String makeAuthorizationUrl() {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("kauth.kakao.com")
                .path("/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .build(true);


        return uriComponents.toUriString();
    }
}
