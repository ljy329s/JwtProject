package com.example.jwtproject.auth;

import com.example.jwtproject.jwt.TokenProvider;
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
        log.info("=======loadUserByUsername 시작===========");
        Member member = memberRepository.selectMember(username);
        if(member==null){
            log.info("유저 없음");
            return null;
        }
        log.info("해당 유저 존재");
        
        return new PrincipalDetails(member);
    }
}
