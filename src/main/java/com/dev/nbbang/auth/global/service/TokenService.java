package com.dev.nbbang.auth.global.service;

public interface TokenService {
    // 토큰 관리
    String manageToken(String memberId, String nickname);
}
