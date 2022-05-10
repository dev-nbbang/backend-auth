package com.dev.nbbang.auth.authentication.repository;

import com.dev.nbbang.auth.authentication.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    // 회원 아이디로 회원 검색
    Member findByMemberId(String memberId);

    // 회원 저장 save 사용 -> Member save(Member member);

}
