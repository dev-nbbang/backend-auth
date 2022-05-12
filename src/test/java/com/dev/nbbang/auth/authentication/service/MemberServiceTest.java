package com.dev.nbbang.auth.authentication.service;

import com.dev.nbbang.auth.authentication.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

class MemberServiceTest {

   /* @Test
    @DisplayName("회원 서비스 : 회원 추가 정보 저장 - 성공")
    void 회원_추가_정보_저장_성공() {
        //given
        Member newMember = testMemberBuilder();
        given(memberRepository.findByMemberId(newMember.getMemberId())).willReturn(null);
        given(memberRepository.save(any())).willReturn(newMember);
        given(ottViewRepository.findAllByOttIdIn(anyList())).willReturn(testOttView());
        given(memberOttRepository.saveAll(anyList())).willReturn(testMemberOtt());
        given(memberRepository.findByMemberId("test")).willReturn(testRecommendMember());
        given(pointRepository.save(any())).willReturn(testPointBuilder());

        // when
        MemberDTO savedMember = memberService.saveMember(newMember, testOttId(), "test");

        //then
        assertThat(savedMember.getMemberId()).isEqualTo("Test Id");
        assertThat(savedMember.getNickname()).isEqualTo("Test Nickname");
        assertThat(savedMember.getPoint()).isEqualTo(0L);
        assertThat(savedMember.getExp()).isEqualTo(0L);
        assertThat(savedMember.getGrade()).isEqualTo(Grade.BRONZE);
        assertThat(savedMember.getPartyInviteYn()).isEqualTo("Y");
        assertThat(savedMember.getMemberOtt().get(0).getOttView().getOttName()).isEqualTo("test");
        assertThat(savedMember.getMemberOtt().get(0).getOttView().getOttImage()).isEqualTo("test.image");
    }

    @Test
    @DisplayName("회원 서비스 : 회원 추가 정보 저장 - 실패")
    void 회원_추가_정보_저장_실패() {
        //given
        given(memberRepository.save(any())).willReturn(null);

        // when
        assertThrows(NoCreateMemberException.class, () -> memberService.saveMember(testMemberBuilder(), testOttId(), "recommend Id"), "회원정보 저장에 실패했습니다.");
    }*/
}