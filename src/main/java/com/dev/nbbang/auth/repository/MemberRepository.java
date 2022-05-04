package com.dev.nbbang.auth.repository;

import com.dev.nbbang.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    // 회원 아이디로 회원 검색
    Member findByMemberId(String memberId);

    // 회원 저장 save 사용

}
