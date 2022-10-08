package com.dev.nbbang.auth.authentication.service;

import com.dev.nbbang.auth.api.entity.SocialLoginType;
import com.dev.nbbang.auth.api.exception.FailGenerateTokenException;
import com.dev.nbbang.auth.api.service.SocialOauth;
import com.dev.nbbang.auth.api.util.SocialLoginIdUtil;
import com.dev.nbbang.auth.api.util.SocialTypeMatcher;
import com.dev.nbbang.auth.authentication.dto.MemberDTO;
import com.dev.nbbang.auth.authentication.entity.Member;
import com.dev.nbbang.auth.authentication.exception.*;
import com.dev.nbbang.auth.authentication.util.NicknameValidation;
import com.dev.nbbang.auth.global.exception.NbbangException;
import com.dev.nbbang.auth.authentication.repository.MemberRepository;
import com.dev.nbbang.auth.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final SocialTypeMatcher socialTypeMatcher;
    private final RedisUtil redisUtil;

    /**
     * 소셜 로그인 타입과 인가코드를 이용해 각 포털의 소셜 로그인을 통해 로그인한다.
     *
     * @param socialLoginType Enum 타입의 소셜 로그인 타입 (Google, kakao)
     * @param code            각 소셜 로그인 콜백 URI에 리턴해주는 인가코드
     * @return memberId  각 포털의 첫번째 이니셜과 제공하는 소셜 로그인 아이디를 합친 String 타입의 고유 아이디
     */
    public String socialLogin(SocialLoginType socialLoginType, String code) {
        final String ACCESS_TOKEN = "access_token";         // 구글, 카카오 엑세스 토큰 키
        final String REFRESH_TOKEN = "refresh_token";       // 구글, 카카오 리프레시 토큰 키
        final String REFRESH_TOKEN_EXPIRES = "refresh_token_expires_in";        // 카카오 리프레시토큰 만료시간 키
        try {
            // 1. 소셜 로그인 타입 매칭
            SocialOauth socialOauth = socialTypeMatcher.findSocialOauthByType(socialLoginType);

            // 2. 토큰 발급
            Map<String, Object> tokenResponse = socialOauth.requestAccessToken(code);
            long expires = 25184000L;

            if(!tokenResponse.containsKey(ACCESS_TOKEN) || !tokenResponse.containsKey(REFRESH_TOKEN))
                throw new FailGenerateTokenException("소셜 로그인 토큰 발급에 실패했습니다.", NbbangException.FAIL_GENERATE_TOKEN);
            String accessToken = tokenResponse.get(ACCESS_TOKEN).toString();
            String refreshToken = tokenResponse.get(REFRESH_TOKEN).toString();

            // 카카오의 경우 리프레시 토큰 기간으로 지정
            if(socialLoginType == SocialLoginType.KAKAO && tokenResponse.containsKey(REFRESH_TOKEN_EXPIRES))
                expires = Integer.parseInt(tokenResponse.get(REFRESH_TOKEN_EXPIRES).toString());

            // 3. 소셜 로그인 시도
            String socialLoginId = socialOauth.requestUserInfo(accessToken);

            // 4. 엔빵 아이디로 변환
            SocialLoginIdUtil socialLoginIdUtil = new SocialLoginIdUtil(socialLoginType, socialLoginId);

            // 5. Redis에 소셜 토큰 관리
            String memberId = socialLoginIdUtil.getMemberId();
            final String SOCIAL_KEY = "social-token:" + memberId;

            redisUtil.setData(SOCIAL_KEY, refreshToken, expires);

            return memberId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new FailSocialLoginException("소셜 로그인에 실패했습니다.", NbbangException.FAIL_SOCIAL_LOGIN);
    }

    /**
     * 회원 아이디를 이용해 가입된 회원 상세 내용을 찾는다.
     *
     * @param memberId JWT 토큰으로 파싱한 회원 아이디
     * @return MemberDTO  회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
    @Override
    public MemberDTO findMember(String memberId) {
        Member member = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        return MemberDTO.create(member);
    }

    @Override
    public MemberDTO saveMember(Member member, List<Integer> ottId, String recommendMemberId) {
        // 1. 회원이 존재하는지 판단
        Optional.ofNullable(memberRepository.findByMemberId(member.getMemberId())).ifPresent(
                exception -> {
                    throw new DuplicateMemberIdException("이미 존재하는 회원입니다.", NbbangException.DUPLICATE_MEMBER_ID);
                }
        );

        // 닉네임 유효성 검증
        if(NicknameValidation.valid(member.getNickname()))
            throw new IllegalNicknameException("옳바르지 않은 닉네임입니다.", NbbangException.ILLEGAL_NICKNAME);

        // 2. 회원 정보 저장
        Member savedMember = Optional.of(memberRepository.save(member))
                .orElseThrow(() -> new NoCreateMemberException("회원정보 저장에 실패했습니다.", NbbangException.NO_CREATE_MEMBER));

        // 4. 저장 완료된 경우 저장된 회원 리턴
        return MemberDTO.create(savedMember);
    }
}
