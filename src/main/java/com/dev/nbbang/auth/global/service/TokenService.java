package com.dev.nbbang.auth.global.service;

public interface TokenService {
    // 토큰 관리
    String manageToken(String memberId, String nickname);

    // 엑세스 토큰이 만료된 경우 리프레시 토큰 확인 후 새롭게 발급
    String reissueToken(String accessToken);
}
