package com.dev.nbbang.auth.global.service;

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
}
