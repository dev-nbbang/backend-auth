package com.dev.nbbang.auth.api.util;


import com.dev.nbbang.auth.api.entity.SocialLoginType;

public class SocialLoginIdUtil {
    private SocialLoginType socialLoginType;
    private String id;
    private String memberId;
    public SocialLoginIdUtil(SocialLoginType socialLoginType, String id) {
        this.socialLoginType = socialLoginType;
        this.id = id;
        this.memberId = makeSocialLoginId(socialLoginType, id);
    }

    public String makeSocialLoginId(SocialLoginType socialLoginType, String id){
        switch (socialLoginType) {
            case KAKAO: return "K-"+id;
            case GOOGLE: return "G-"+id;
            default:    return "test-"+id;
        }
    }

    public String getMemberId() {
        return memberId;
    }
}
