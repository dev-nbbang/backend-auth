package com.dev.nbbang.auth.controller;

import com.dev.nbbang.auth.api.dto.AuthResponse;
import com.dev.nbbang.auth.api.entity.SocialLoginType;
import com.dev.nbbang.auth.api.exception.FailCreateAuthUrlException;
import com.dev.nbbang.auth.api.exception.IllegalSocialTypeException;
import com.dev.nbbang.auth.api.util.SocialAuthUrl;
import com.dev.nbbang.auth.api.util.SocialTypeMatcher;
import com.dev.nbbang.auth.dto.MemberDTO;
import com.dev.nbbang.auth.dto.response.MemberLoginInfoResponse;
import com.dev.nbbang.auth.exception.NoSuchMemberException;
import com.dev.nbbang.auth.global.response.CommonResponse;
import com.dev.nbbang.auth.service.MemberService;
import com.dev.nbbang.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final MemberService memberService;
    private final SocialTypeMatcher socialTypeMatcher;
    private final JwtUtil jwtUtil;

    @GetMapping(value = "/{socialLoginType}/test")
//    @Operation(summary = "백엔드 소셜 로그인 인가 코드 요청", description = "백엔드 소셜 로그인 인가 코드 요청 테스트")
    public void test(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType, HttpServletResponse httpServletResponse) throws IOException {
        SocialAuthUrl socialAuthUrl = socialTypeMatcher.findSocialAuthUrlByType(socialLoginType);
        String authUrl = socialAuthUrl.makeAuthorizationUrl();

        System.out.println("authUrl = " + authUrl);
        httpServletResponse.sendRedirect(authUrl);
    }

    @GetMapping(value = "/{socialLoginType}")
//    @Operation(summary = "소셜 로그인 인가코드 URL", description = "소셜 로그인 인가코드 URL을 생성한다.")
    public ResponseEntity<?> socialLoginType(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);

        try {
            // PathVariable의 소셜 타입을 인가 코드 URL 생성 (카카오, 구글)
            SocialAuthUrl socialAuthUrl = socialTypeMatcher.findSocialAuthUrlByType(socialLoginType);
            String authUrl = socialAuthUrl.makeAuthorizationUrl();

            return ResponseEntity.ok(AuthResponse.create(authUrl));
        } catch (IllegalSocialTypeException | FailCreateAuthUrlException e) {
            log.info(" >> [Nbbang Member Controller - signUp] : " + e.getMessage());
            return ResponseEntity.ok(CommonResponse.create(false, e.getMessage()));
        }
    }

    @GetMapping(value = "/{socialLoginType}/callback")
//    @Operation(summary = "동의 정보 인증 후 리다이렉트", description = "동의 정보 인증 후 리다이렉트 URI")
    public ResponseEntity<?> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                      @RequestParam(name = "code") String code, HttpServletResponse servletResponse) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);

        // 프론트 -> 소셜 서버 -> 리다이렉트 -> 프론트는 결과를 모름
        // 소셜 로그인 실패시
        String memberId = memberService.socialLogin(socialLoginType, code);
        if (memberId == null) {
            log.info("badRequest");
            // Message 넘기기
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            MemberDTO findMember = memberService.findMember(memberId);

            // 회원 닉네임 수정 시 JWT 새로 생성 및 레디스 값 갱신 (프론트
            // 구현 후 넣어주기)
            String accessToken = memberService.manageToken(findMember);
            servletResponse.setHeader("Authorization", "Bearer " + accessToken);
            System.out.println("accessToken = " + accessToken);

            return new ResponseEntity<>(MemberLoginInfoResponse.create(findMember, true,true, "소셜 로그인에 성공했습니다."), HttpStatus.OK);
        } catch (NoSuchMemberException e) {
            log.info(e.getMessage());
            log.info("회원가입필요");

            return new ResponseEntity<>(MemberRegisterResponse.create(memberId, false), HttpStatus.OK);
        }
    }
}
