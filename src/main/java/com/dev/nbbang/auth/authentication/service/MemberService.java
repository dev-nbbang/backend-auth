package com.dev.nbbang.auth.authentication.service;


import com.dev.nbbang.auth.api.entity.SocialLoginType;
import com.dev.nbbang.auth.authentication.dto.MemberDTO;
import com.dev.nbbang.auth.authentication.entity.Member;

import java.util.List;

public interface MemberService {
    // 기본적인 회원 정보만 전달 받아서 파싱 후 저장 나머지 이벤트 발생해서 회원 서비스 로직에서 처리?
//    MemberDTO saveMember(Member member, List<Integer> ottId, String recommendMemberId);
//    MemberDTO saveMember(Member member);

    // 소셜 로그인 시도
    String socialLogin(SocialLoginType socialLoginType, String code);

    // 아이디로 회원 찾기
    MemberDTO findMember(String memberId);

    // 회원 추가 정보 저장
    MemberDTO saveMember(Member member, List<Integer> ottId, String recommendMemberId);

}

