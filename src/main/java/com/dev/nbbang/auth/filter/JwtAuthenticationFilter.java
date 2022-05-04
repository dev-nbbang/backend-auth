package com.dev.nbbang.auth.filter;

import com.dev.nbbang.auth.dto.request.MemberRegisterRequest;
import com.dev.nbbang.auth.util.JwtUtil;
import com.dev.nbbang.auth.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest)request;
            String token = req.getHeader("Authorization");
            if(!token.isEmpty() && token.startsWith("Bearer ") && !jwtUtil.isTokenExpired(token)) {
                // Bearer 파싱
                token = token.substring(7);

                logger.info("토큰 값 존재, Bearer로시작 token >> " + token);

                // 회원 아이디 얻기
                String memberId = jwtUtil.getMemberId(token);

                // 인증
                Authentication authentication = jwtUtil.getAuthentication(token);

                // 인증 정보 세팅
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch(ExpiredJwtException e) {
            String memberId = e.getClaims().get("memberId", String.class);
            String refreshToken = redisUtil.getData(memberId);

            if(!refreshToken.isEmpty() && jwtUtil.isTokenExpired(refreshToken)) {
                if(memberId.equals(jwtUtil.getMemberId(refreshToken))) {
                    // 리프레시 토큰을 통해 새로운 토큰 생성
                    String newToken = jwtUtil.generateAccessToken(memberId, jwtUtil.getNickname(refreshToken));

                    // 새로운 토큰으로 인증
                    Authentication authentication = jwtUtil.getAuthentication(newToken);

                    // 인증 정보 세팅
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 헤더에 새로운 토큰 담아주기
                    HttpServletResponse res = (HttpServletResponse) response;
                    res.addHeader("Authorization", "Bearer " + newToken);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        chain.doFilter(request, response);
    }
}
