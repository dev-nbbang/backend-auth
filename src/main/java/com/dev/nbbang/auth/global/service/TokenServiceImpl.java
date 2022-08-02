package com.dev.nbbang.auth.global.service;

import com.dev.nbbang.auth.authentication.dto.response.TokenResponse;
import com.dev.nbbang.auth.global.exception.ExpiredRefreshTokenException;
import com.dev.nbbang.auth.global.exception.NbbangException;
import com.dev.nbbang.auth.global.util.JwtUtil;
import com.dev.nbbang.auth.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public TokenResponse manageToken(String memberId, String nickname) {
        String accessToken = jwtUtil.generateAccessToken(memberId, nickname);
        String refreshToken = jwtUtil.generateRefreshToken(memberId, nickname);
        redisUtil.setData(memberId+":access", accessToken, JwtUtil.TOKEN_VALIDATION_SECOND);
        redisUtil.setData(memberId, refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
        return TokenResponse.create(accessToken, refreshToken);
    }

    /**
     * JWT AccessToken 만료시 RefreshToken 유효 여부를 판단 후 재 발급한다.
     *
     * @param accessToken 엑세스 토큰
     * @return accessToken을 재발급
     */
    @Override
    public TokenResponse reissueToken(String accessToken, String refreshToken) {
        try {
            // 1. 회원 아이디 파싱
            String memberId = jwtUtil.getMemberId(refreshToken);
            // 2. 회원 닉네임 파싱
            String nickname = jwtUtil.getNickname(refreshToken);

            // 3 Access Token 유효 여부 판단 및 만료 처리
            if (redisUtil.hasKey(memberId+":access")) {
                redisUtil.deleteData(memberId+":access");
                redisUtil.deleteData(memberId);
                throw new ExpiredRefreshTokenException("재 로그인이 필요합니다.", NbbangException.EXPIRED_REFRESH_TOKEN);
            }
            // 4. 회원 아이디와 닉네임으로 새로운 토큰 생성
            return manageToken(memberId, nickname);
        } catch (Exception e) {
            throw new ExpiredRefreshTokenException("재 로그인이 필요합니다.", NbbangException.EXPIRED_REFRESH_TOKEN);
        }
    }
}
