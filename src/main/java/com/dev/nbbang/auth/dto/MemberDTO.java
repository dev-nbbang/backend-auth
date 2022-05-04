package com.dev.nbbang.auth.dto;

import com.dev.nbbang.auth.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberDTO {
    private String memberId;
    private String nickname;

    @Builder
    public MemberDTO(String memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }

    public static MemberDTO create(Member member) {
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .build();
    }
}
