package com.dev.nbbang.auth.authentication.service;

import com.dev.nbbang.auth.api.util.SocialTypeMatcher;
import com.dev.nbbang.auth.authentication.dto.MemberDTO;
import com.dev.nbbang.auth.authentication.entity.Member;
import com.dev.nbbang.auth.authentication.exception.DuplicateMemberIdException;
import com.dev.nbbang.auth.authentication.exception.NoCreateMemberException;
import com.dev.nbbang.auth.authentication.exception.NoSuchMemberException;
import com.dev.nbbang.auth.authentication.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SocialTypeMatcher socialTypeMatcher;

    @Mock
    private MemberProducer memberProducer;

    @InjectMocks
    private MemberServiceImpl memberService;


    @Test
    @DisplayName("인증 서비스 : 회원 아이디를 이용해 회원 조회 성공")
    void 회원_아이디로_회원_조회_성공() {
        // given
        given(memberRepository.findByMemberId(anyString())).willReturn(testMember());

        // when
        MemberDTO findMember = memberService.findMember("testId");

        // then
        assertThat(findMember.getMemberId()).isEqualTo("testId");
        assertThat(findMember.getNickname()).isEqualTo("testNickname");
    }

    @Test
    @DisplayName("인증 서비스 : 회원 아이디를 이용해 회원 조회 실패")
    void 회원_아이디로_회원_조회_실패() {
        // given
        given(memberRepository.findByMemberId(anyString())).willThrow(NoSuchMemberException.class);

        // then
        assertThrows(NoSuchMemberException.class, () -> memberService.findMember("no Id"));
    }

    @Test
    @DisplayName("인증 서비스 : 회원 추가 정보 저장 성공")
    void 회원_추가_정보_저장_성공() throws JsonProcessingException {
        // 카프카 로직
        // 1. 회원이 존재하는 지 판단
        given(memberRepository.findByMemberId(anyString())).willReturn(null);

        // 2. 회원 정보 저장
        given(memberRepository.save(any())).willReturn(testMember());

        // 3. 회원 정보 저장 시 카프카 메시지 발송
        doNothing().when(memberProducer).sendRecommendIdAndOttId(any());

        // when
        MemberDTO savedMember = memberService.saveMember(testMember(), new ArrayList<>(Arrays.asList(1, 2)), "recommendId");

        // then
        assertThat(savedMember.getMemberId()).isEqualTo("testId");
        assertThat(savedMember.getNickname()).isEqualTo("testNickname");
    }

    @Test
    @DisplayName("인증 서비스 : 회원 추가 정보 저장 실패")
    void 회원_추가_정보_저장_실패_회원존재경우() {
        // given
        given(memberRepository.findByMemberId(anyString())).willThrow(DuplicateMemberIdException.class);

        // then
        assertThrows(DuplicateMemberIdException.class, () -> memberService.saveMember(testMember(), new ArrayList<>(Arrays.asList(1, 2)), "recommendId"));
    }

    @Test
    @DisplayName("인증 서비스 : 회원 추가 정보 저장 실패")
    void 회원_추가_정보_저장_실패_저장단계실패() {
        // given
        given(memberRepository.findByMemberId(anyString())).willReturn(null);
        given(memberRepository.save(any())).willThrow(NoCreateMemberException.class);

        // then
        assertThrows(NoCreateMemberException.class, () -> memberService.saveMember(testMember(), new ArrayList<>(Arrays.asList(1, 2)), "recommendId"));
    }

    private static Member testMember() {
        return Member.builder()
                .memberId("testId")
                .nickname("testNickname")
                .build();
    }
}