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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {
    private final MemberRepository memberRepository;
    private final SocialTypeMatcher socialTypeMatcher;

    /**
     * 소셜 로그인 타입과 인가코드를 이용해 각 포털의 소셜 로그인을 통해 로그인한다.
     *
     * @param socialLoginType Enum 타입의 소셜 로그인 타입 (Google, kakao)
     * @param code            각 소셜 로그인 콜백 URI에 리턴해주는 인가코드
     * @return memberId  각 포털의 첫번째 이니셜과 제공하는 소셜 로그인 아이디를 합친 String 타입의 고유 아이디
     */
    public String socialLogin(SocialLoginType socialLoginType, String code) {
        try {
            SocialOauth socialOauth = socialTypeMatcher.findSocialOauthByType(socialLoginType);
            String socialLoginId = socialOauth.requestUserInfo(code);
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

        // 4. 저장 완료된 경우 저장된 회원 리턴
        return MemberDTO.create(savedMember);
    }

    /**
     * @param username 회원 아이디
     * @return User
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(username)).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
        authorityList.add(new SimpleGrantedAuthority("USER"));

        return new User(findMember.getMemberId(), "", authorityList);
    }
}
