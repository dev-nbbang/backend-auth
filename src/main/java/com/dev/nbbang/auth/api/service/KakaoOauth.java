package com.dev.nbbang.auth.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@RefreshScope
public class KakaoOauth implements SocialOauth {
    private final RestTemplate restTemplate;

    // 주입해준 value 값들 공통으로 정의할 수 있는지 확인해보기
    @Value("${sns.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${sns.kakao.client-id}")
    private String clientId;

    @Value("${sns.kakao.client-secret}")
    private String clientSecret;

    @Value("${sns.kakao.token-uri}")
    private String tokenUri;

    @Value("${sns.kakao.user-info-uri}")
    private String userInfoUri;

    // 1. 카카오 서버에서 접근 가능한 엑세스 토큰 발급받기
    @Override
    public String requestAccessToken(String code) {
        // Http Header 세팅
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Http Body 세팅
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        // RequestBody 만들기
        HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity = new HttpEntity<>(params, httpHeaders);

        // Response
        try {
            ResponseEntity<String> kakaoResponse = restTemplate.postForEntity(tokenUri, kakaoRequestEntity, String.class);

            if (kakaoResponse.getStatusCode() == HttpStatus.OK) {
                // JSon Response 파싱
                ObjectMapper objectMapper = new ObjectMapper();

                Map<String, String> tokenResponse = objectMapper.readValue(kakaoResponse.getBody(), Map.class);

                return tokenResponse.get("access_token");
            }
        } catch (IOException e) {
            // 커스텀 예외 처리
            return null;
        }

        // 예외 처리 시 어떤 값을 던질지 고민
        return null;
    }

    // 2. 카카오 서버에서 회원 정보 불러오기
    @Override
    public String requestUserInfo(String code) {
        String accessToken = requestAccessToken(code);

        // Http Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> kakaoRequestEntity = new HttpEntity<>(httpHeaders);
        try {
            // Post 요청
            ResponseEntity<String> kakaoResponse = restTemplate.postForEntity(userInfoUri, kakaoRequestEntity, String.class);

            if (kakaoResponse.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> kakaoUser = objectMapper.readValue(kakaoResponse.getBody(), Map.class);
                return kakaoUser.get("id").toString();
            }
        } catch (IOException e) {
            // 커스텀 예외 던지기
            return null;
        }
        // 디폴트 예외 뭐로 던질지
        return null;
    }

}
