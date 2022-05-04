package com.dev.nbbang.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    public final static long TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 2;
    public final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 *24 * 2;

    private final UserDetailsService userDetailsService;

    @Value("${spring.jwt.secret}")
    private String SECRET_KEY;

    // Token 생성 메소드
    private String generateToken(String memberId, String nickname, long expireSecond) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);       // JWT 토큰 페이로드에 회원 아이디 추가
        claims.put("nickname", nickname);     // JWT 토큰페이로드에 닉네임 추가

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireSecond))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    // RefreshToken 생성
    public String generateRefreshToken(String memberId, String nickname) {
        return generateToken(memberId, nickname, REFRESH_TOKEN_VALIDATION_SECOND);
    }

    // AccesssToken 생성
    public String generateAccessToken(String memberId, String nickname ) {
        return generateToken(memberId, nickname, TOKEN_VALIDATION_SECOND);
    }

    // 발췌한 payload에서 userid 추출
    public String getMemberId
    (String token) {
        return extractAllClaims(token).get("memberId", String.class);
    }

    // 발췌한 페이로드에서 nickname 추출
    public String getNickname(String token) {
        return extractAllClaims(token).get("nickname", String.class);
    }

    /**
     *  JWT Payload에 담는 정보의 한 '조각'을 Claim이라 한다.
     *  Jwt Parser를 빌드하고 Parser에 토큰 넣어서 payload(body) 부분 발췌
     */
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * secretKey 해싱 키로 만들기
     */
    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰이 만료되었는지 확인
    public Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // 인증 토큰 생성
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getMemberId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
