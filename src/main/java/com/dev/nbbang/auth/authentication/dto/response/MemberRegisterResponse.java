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
    private boolean isRegister;

    public static MemberRegisterResponse create(String memberId, boolean isRegister) {
        return MemberRegisterResponse.builder()
                .memberId(memberId)
                .isRegister(isRegister)
                .build();
    }

}
