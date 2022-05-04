package com.dev.nbbang.auth.service;

import com.dev.nbbang.auth.dto.MemberDTO;
import com.dev.nbbang.auth.entity.Member;
import com.dev.nbbang.auth.repository.MemberRepository;
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

    @Override
    public MemberDTO saveMember(Member member) {
        try {
            // 커스텀 예외 처리
            Member savedMember = Optional.of(memberRepository.save(member)).orElseThrow(Exception::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member findMember = Optional.ofNullable(memberRepository.findByMemberId(username)).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
        authorityList.add(new SimpleGrantedAuthority("USER"));

        return new User(findMember.getMemberId(), "", authorityList);
    }
}
