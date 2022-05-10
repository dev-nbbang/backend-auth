package com.dev.nbbang.auth.global.service;

import com.dev.nbbang.auth.global.exception.ExpiredRefreshTokenException;
import com.dev.nbbang.auth.global.exception.NbbangException;
import com.dev.nbbang.auth.global.util.JwtUtil;
import com.dev.nbbang.auth.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    /**
     * JWT AccessToken과 RefreshToken을 발급한다.
     *
     * @param memberId 회원 아이디
     * @param nickname 회원 닉네임
     * @return accessToken String 타입의 엑세스 토큰
     */
    @Override
    public String manageToken(String memberId, String nickname) {
        String refreshToken = jwtUtil.generateRefreshToken(memberId, nickname);
        redisUtil.setData(memberId, refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

        return jwtUtil.generateAccessToken(memberId, nickname);
    }

    /**
     * JWT AccessToken 만료시 RefreshToken 유효 여부를 판단 후 재 발급한다.
     *
     * @param accessToken 엑세스 토큰
     * @return accessToken을 재발급
     */
    @Override
    public String reissueToken(String accessToken) {
        // 1. 회원 아이디 파싱
        String memberId = jwtUtil.getMemberId(accessToken);

        // 2. 회원 닉네임 파싱
        String nickname = jwtUtil.getNickname(accessToken);

        // 3 Refresh Token 유효 여부 판단
        if(!redisUtil.hasKey(memberId))
            throw new ExpiredRefreshTokenException("재 로그인이 필요합니다.", NbbangException.EXPIRED_REFRESH_TOKEN);

        // 4. 회원 아이디와 닉네임으로 새로운 토큰 생성
        return manageToken(memberId, nickname);
    }
}
