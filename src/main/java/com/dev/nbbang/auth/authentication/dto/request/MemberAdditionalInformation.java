package com.dev.nbbang.auth.authentication.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberAdditionalInformation {
    private String memberId;
    private String recommendMemberId;
    private List<Integer> ottId;

    @Builder
    public MemberAdditionalInformation(String memberId, String recommendMemberId, List<Integer> ottId) {
        this.memberId = memberId;
        this.recommendMemberId = recommendMemberId;
        this.ottId = ottId;
    }

    public static MemberAdditionalInformation create(String memberId, String recommendMemberId, List<Integer> ottId) {
        return MemberAdditionalInformation.builder()
                .memberId(memberId)
                .recommendMemberId(recommendMemberId)
                .ottId(ottId)
                .build();
    }
}
