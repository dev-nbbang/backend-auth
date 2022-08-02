package com.dev.nbbang.auth.authentication.controller;

import com.dev.nbbang.auth.api.exception.IllegalSocialTypeException;
import com.dev.nbbang.auth.api.util.KakaoAuthUrl;
import com.dev.nbbang.auth.api.util.SocialAuthUrl;
import com.dev.nbbang.auth.api.util.SocialTypeMatcher;
import com.dev.nbbang.auth.authentication.dto.MemberDTO;
import com.dev.nbbang.auth.authentication.dto.request.MemberRegisterRequest;
import com.dev.nbbang.auth.authentication.dto.response.TokenResponse;
import com.dev.nbbang.auth.authentication.entity.Grade;
import com.dev.nbbang.auth.authentication.exception.NoCreateMemberException;
import com.dev.nbbang.auth.authentication.exception.NoSuchMemberException;
import com.dev.nbbang.auth.authentication.service.MemberService;
import com.dev.nbbang.auth.global.exception.ExpiredRefreshTokenException;
import com.dev.nbbang.auth.global.exception.NbbangException;
import com.dev.nbbang.auth.global.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    @MockBean
    private SocialTypeMatcher socialTypeMatcher;

    @MockBean
    private MemberService memberService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private SocialAuthUrl socialAuthUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("인증 컨트롤러 : 소셜 로그인 인가코드 URL 생성 성공")
    void 소셜_로그인_인가코드_URL_생성_성공() throws Exception {
        //given
        String uri = "/auth/kakao";
        given(socialTypeMatcher.findSocialAuthUrlByType(any())).willReturn(new KakaoAuthUrl());
        given(socialAuthUrl.makeAuthorizationUrl()).willReturn("https://kauth.kakao.com/oauth/authorize?client_id&redirect_uri&response_type=code");

        //when
        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.authUrl").value("https://kauth.kakao.com/oauth/authorize?client_id&redirect_uri&response_type=code"))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("인증 컨트롤러 : 소셜 로그인 인가코드 URL 생성 실패")
    void 소셜_로그인_인가코드_생성_실패() throws Exception {
        //given
        String uri = "/auth/kakao";
        given(socialTypeMatcher.findSocialAuthUrlByType(any())).willThrow(new IllegalSocialTypeException("소셜 실패", NbbangException.ILLEGAL_SOCIAL_TYPE));

        MockHttpServletResponse response = mvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("인증 컨트롤러 : 소셜 로그인 실패")
    void 소셜_로그인_실패() throws Exception {
        // given
        String uri = "/auth/kakao/callback";
        given(memberService.socialLogin(any(), anyString())).willReturn(null);

        // when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .param("code", "testCode"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse();


        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("인증 컨트롤러 : 소셜 로그인 성공")
    void 소셜_로그인_성공() throws Exception {
        //given
        String uri = "/auth/kakao/callback";
        given(memberService.socialLogin(any(), anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willReturn(testMemberDTO());
        given(tokenService.manageToken(anyString(), anyString())).willReturn(tokenResponse());

        //when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .param("code", "testCode"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.grade").value("BRONZE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.exp").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.point").value(0))
                .andDo(print())
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer testToken");
    }

    @Test
    @DisplayName("인증 컨트롤러 : 소셜 로그인 회원 없는 경우 성공")
    void 소셜_로그인_회원_없는_경우_성공() throws Exception {
        // given
        String uri = "/auth/kakao/callback";
        given(memberService.socialLogin(any(), anyString())).willReturn("testId");
        given(memberService.findMember(anyString())).willThrow(NoSuchMemberException.class);

        //when
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get(uri)
                .param("code", "testCode"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value("testId"))
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("인증 컨트롤러 : 추가 회원 가입 실패")
    void 추가_회원_가입_실패() throws Exception {
        // given
        String uri = "/auth/new";
        given(memberService.saveMember(any(), anyList(), anyString())).willThrow(new NoCreateMemberException("회원 가입 실패", NbbangException.NO_CREATE_MEMBER));

        //when
        MockHttpServletResponse response = mvc.perform(
                post(uri)
//                        .with(csrf())
                        .content(objectMapper.writeValueAsString(testRegisterMember()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("인증 컨트롤러 : 추가 회원 가입 성공")
    void 추가_회원_가입_성공() throws Exception {
        // given
        String uri = "/auth/new";
        given(memberService.saveMember(any(), anyList(), anyString())).willReturn(testMemberDTO());
//        given(tokenService.manageToken(anyString(), anyString())).willReturn("new Token");


        //when
        MockHttpServletResponse response = mvc.perform(
                post(uri)
//                        .with(csrf())
                        .content(objectMapper.writeValueAsString(testRegisterMember()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value("testId"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value("testNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.grade").value("BRONZE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.exp").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.point").value(0))
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer new Token");
    }

    @Test
    @DisplayName("인증 컨트롤러 : 엑세스 토큰 재발급 성공")
    void 엑세스_토큰_재발급_성공() throws Exception {
        // given
        String uri = "/auth/reissue";
        given(tokenService.reissueToken(anyString(), anyString())).willReturn(tokenResponse());

        //when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "test token"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer new token");
    }

    @Test
    @DisplayName("인증 컨트롤러 : 엑세스 토큰 재발급 실패")
    void 엑세스_토큰_재발급_실패() throws Exception {
        // gien
        String uri = "/auth/reissue";
        given(tokenService.reissueToken(anyString(), anyString())).willThrow(new ExpiredRefreshTokenException("만료된 리프레시 토큰", NbbangException.EXPIRED_REFRESH_TOKEN));

        //when
        MockHttpServletResponse response = mvc.perform(get(uri)
                .header("Authorization", "test token"))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private static MemberDTO testMemberDTO() {
        return MemberDTO.builder().memberId("testId")
                .nickname("testNickname")
                .grade(Grade.BRONZE)
                .exp(0L)
                .point(0L)
                .build();
    }

    private static TokenResponse tokenResponse() {
        return TokenResponse.create("test", "test");
    }

    private static MemberRegisterRequest testRegisterMember() {
        List<Integer> ottId = new ArrayList<>();
        ottId.add(1);
        return MemberRegisterRequest.builder().memberId("testId")
                .nickname("testNickname")
                .ottId(ottId)
                .recommendMemberId("test Recommend Id")
                .build();
    }
}