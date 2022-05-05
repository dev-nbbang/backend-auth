package com.dev.nbbang.auth.dto.request;

import com.dev.nbbang.auth.authentication.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterRequest {
    private String memberId;
    private String nickname;
    private List<Integer> ottId;
    private String recommendMemberId;

    public static Member toEntity(MemberRegisterRequest request) {
        return Member.builder()
                .memberId(request.getMemberId())
                .nickname(request.getNickname())
                .build();
    }
}
