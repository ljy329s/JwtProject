package com.example.jwtproject.auth;

import com.example.jwtproject.model.domain.Member;
import com.example.jwtproject.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.selectMember(username);
        if (member != null) {
            return new PrincipalDetails(member);
        }
        log.info("해당 아이디가 없습니다");
        return null;
    }
}
