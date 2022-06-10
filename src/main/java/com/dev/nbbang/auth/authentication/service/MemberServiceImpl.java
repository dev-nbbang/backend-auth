package com.dev.nbbang.auth.authentication.service;

import com.dev.nbbang.auth.api.entity.SocialLoginType;
import com.dev.nbbang.auth.api.service.SocialOauth;
import com.dev.nbbang.auth.api.util.SocialLoginIdUtil;
import com.dev.nbbang.auth.api.util.SocialTypeMatcher;
import com.dev.nbbang.auth.authentication.dto.MemberDTO;
import com.dev.nbbang.auth.authentication.entity.Member;
import com.dev.nbbang.auth.authentication.exception.DuplicateMemberIdException;
import com.dev.nbbang.auth.authentication.exception.NoCreateMemberException;
import com.dev.nbbang.auth.global.exception.NbbangException;
import com.dev.nbbang.auth.authentication.exception.NoSuchMemberException;
import com.dev.nbbang.auth.authentication.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final SocialTypeMatcher socialTypeMatcher;
    private final MemberProducer memberProducer;

    /**
     * 소셜 로그인 타입과 인가코드를 이용해 각 포털의 소셜 로그인을 통해 로그인한다.
     *
     * @param socialLoginType Enum 타입의 소셜 로그인 타입 (Google, kakao)
     * @param code            각 소셜 로그인 콜백 URI에 리턴해주는 인가코드
     * @return memberId  각 포털의 첫번째 이니셜과 제공하는 소셜 로그인 아이디를 합친 String 타입의 고유 아이디
     */
    public String socialLogin(SocialLoginType socialLoginType, String code) {
        try {
            // 1. 소셜 로그인 타입 매칭
            SocialOauth socialOauth = socialTypeMatcher.findSocialOauthByType(socialLoginType);

            // 2. 소셜 로그인 시도
            String socialLoginId = socialOauth.requestUserInfo(code);

            // 3. 엔빵 아이디로 변환
            SocialLoginIdUtil socialLoginIdUtil = new SocialLoginIdUtil(socialLoginType, socialLoginId);
            return socialLoginIdUtil.getMemberId();
        } catch (Exception e) {
            e.printStackTrace();
            return "소셜 로그인 실패";
        }
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
                exception -> { throw new DuplicateMemberIdException("이미 존재하는 회원입니다.", NbbangException.DUPLICATE_MEMBER_ID); }
        );

        // 2. 회원 정보 저장
        Member savedMember = Optional.of(memberRepository.save(member))
                .orElseThrow(() -> new NoCreateMemberException("회원정보 저장에 실패했습니다.", NbbangException.NO_CREATE_MEMBER));

        // 3. 회원 정보 저장 시 카프카 메세지 전달 동기 방식 처리?
        // 트랜잭션 처리 이슈 생각해봐야할듯 ...
        if ((!ottId.isEmpty() && savedMember.getMemberId().length() > 0) || recommendMemberId.length() > 0) {
            try {
                memberProducer.sendRecommendIdAndOttId(MemberProducer.KafkaSendRequest.create(savedMember.getMemberId(), recommendMemberId, ottId));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // 4. 저장 완료된 경우 저장된 회원 리턴
        return MemberDTO.create(savedMember);
    }
}
