package com.dev.nbbang.auth.global.service;

import com.dev.nbbang.auth.authentication.dto.response.TokenResponse;

public interface TokenService {
    // 토큰 관리
    TokenResponse manageToken(String memberId, String nickname);

    // 엑세스 토큰이 만료된 경우 리프레시 토큰 확인 후 새롭게 발급
    TokenResponse reissueToken(String accessToken, String refreshToken);
}
