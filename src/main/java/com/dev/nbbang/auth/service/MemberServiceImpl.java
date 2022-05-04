package com.dev.nbbang.auth.service;

import com.dev.nbbang.auth.api.entity.SocialLoginType;
import com.dev.nbbang.auth.api.service.SocialOauth;
import com.dev.nbbang.auth.api.util.SocialLoginIdUtil;
import com.dev.nbbang.auth.api.util.SocialTypeMatcher;
import com.dev.nbbang.auth.dto.MemberDTO;
import com.dev.nbbang.auth.entity.Member;
import com.dev.nbbang.auth.global.exception.NbbangException;
import com.dev.nbbang.auth.exception.NoSuchMemberException;
import com.dev.nbbang.auth.repository.MemberRepository;
import com.dev.nbbang.auth.util.JwtUtil;
import com.dev.nbbang.auth.util.RedisUtil;
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
public class MemberServiceImpl  implements MemberService, UserDetailsService {
    private final MemberRepository memberRepository;
    private final SocialTypeMatcher socialTypeMatcher;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    /**
     * 소셜 로그인 타입과 인가코드를 이용해 각 포털의 소셜 로그인을 통해 로그인한다.
     *
     * @param socialLoginType  Enum 타입의 소셜 로그인 타입 (Google, kakao)
     * @param code             각 소셜 로그인 콜백 URI에 리턴해주는 인가코드
     * @return memberId  각 포털의 첫번째 이니셜과 제공하는 소셜 로그인 아이디를 합친 String 타입의 고유 아이디
     */
    public String socialLogin(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = socialTypeMatcher.findSocialOauthByType(socialLoginType);
        try {
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
     * @param memberId  JWT 토큰으로 파싱한 회원 아이디
     * @return MemberDTO  회원의 상세내용 (memberId, nickname, bankId, bankAccount, grade, point, exp, billingKey, partyInviteYn, memberOtt)
     */
    @Override
    public MemberDTO findMember(String memberId) {
        Member member = Optional.ofNullable(memberRepository.findByMemberId(memberId))
                .orElseThrow(() -> new NoSuchMemberException("회원이 존재하지 않습니다.", NbbangException.NOT_FOUND_MEMBER));

        return MemberDTO.create(member);
    }

    /**
     * DTO 타입의 회원 객체를 이용해 Redis에서 관리할 리프레시 토큰과 세션에 저장할 엑세스 토큰을 관리한다.
     *
     * @param member DTO 타입의 회원 객체
     * @return accessToken String 타입의 엑세스 토큰
     */
    @Override
    public String manageToken(MemberDTO member) {
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getNickname());
        redisUtil.setData(member.getMemberId(), refreshToken, JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

        return jwtUtil.generateAccessToken(member.getMemberId(), member.getNickname());
    }

    /**
     *
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
