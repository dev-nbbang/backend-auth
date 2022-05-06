package com.dev.nbbang.auth.authentication.dto.response;


import com.dev.nbbang.auth.authentication.dto.MemberDTO;
import com.dev.nbbang.auth.authentication.entity.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberLoginInfoResponse {
    private String memberId;
    private String nickname;
    private Grade grade;
    private Long point;
    private Long exp;

    public static Map<String, Object> create(MemberDTO member, boolean status, String message) {
        MemberLoginInfoResponse memberInfo = MemberLoginInfoResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .grade(member.getGrade())
                .point(member.getPoint())
                .exp(member.getExp())
                .build();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberInfo", memberInfo);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", status);
        responseMap.put("message", message);
        responseMap.put("data", dataMap);

        return responseMap;
    }

   /* public static MemberDefaultInfoResponse create(MemberDTO member, boolean isRegister, String message) {
        return MemberDefaultInfoResponse.builder().memberId(member.getMemberId())
                .nickname(member.getNickname())
                .grade(member.getGrade())
                .point(member.getPoint())
                .exp(member.getExp())
                .ottView(getOttView(member))
                .isRegister(isRegister)
                .message(message)
                .build();
    }*/

/*    private static List<OttView> getOttView(MemberDTO member) {
        List<OttView> ottView = new ArrayList<>();
        for (MemberOtt memberOtt : member.getMemberOtt()) {
            ottView.add(memberOtt.getOttView());
        }

        return ottView;
    }*/
}
