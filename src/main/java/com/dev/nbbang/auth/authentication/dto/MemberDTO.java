package com.dev.nbbang.auth.dto;

import com.dev.nbbang.auth.authentication.entity.Grade;
import com.dev.nbbang.auth.authentication.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberDTO {
    private String memberId;
    private String nickname;
    private Grade grade;
    private Long point;
    private Long exp;

    @Builder
    public MemberDTO(String memberId, String nickname, Grade grade, Long point, Long exp) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.grade = grade;
        this.point = point;
        this.exp = exp;
    }

    public static MemberDTO create(Member member) {
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .grade(member.getGrade())
                .point(member.getPoint())
                .exp(member.getExp())
                .build();
    }
}
