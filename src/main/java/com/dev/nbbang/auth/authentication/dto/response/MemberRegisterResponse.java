package com.dev.nbbang.auth.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRegisterResponse {
    private String memberId;

    public static MemberRegisterResponse create(String memberId) {
        return MemberRegisterResponse.builder()
                .memberId(memberId)
                .build();
    }
}
