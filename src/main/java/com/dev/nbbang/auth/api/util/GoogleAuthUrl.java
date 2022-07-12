package com.dev.nbbang.auth.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RefreshScope
public class GoogleAuthUrl implements SocialAuthUrl{
    @Value("${sns.google.callback.uri}")
    private String redirectUri;

    @Value("${sns.google.client.id}")
    private String clientId;

    @Value("${sns.google.uri}")
    private String baseUri;

    @Override
    public String makeAuthorizationUrl() {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", "profile");
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUri);
        params.put("access_type", "offline");

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));
        return baseUri + "?" + parameterString;
    }
}