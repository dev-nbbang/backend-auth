package com.dev.nbbang.auth.service;


import com.dev.nbbang.auth.dto.MemberDTO;
import com.dev.nbbang.auth.entity.Member;

import java.util.List;

public interface MemberService {
    // 기본적인 회원 정보만 전달 받아서 파싱 후 저장 나머지 이벤트 발생해서 회원 서비스 로직에서 처리?
//    MemberDTO saveMember(Member member, List<Integer> ottId, String recommendMemberId);
    MemberDTO saveMember(Member member);
}

