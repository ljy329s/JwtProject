package com.example.jwtproject.auth;

import com.example.jwtproject.jwt.TokenProvider;
import com.example.jwtproject.model.domain.Member;
import com.example.jwtproject.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=======loadUserByUsername 시작===========");
        Member member = memberRepository.selectMember(username);
        if(member==null){
            System.out.println("로그인 실패");
            return null;
        }
        System.out.println("로그인 성공");
        
        return new PrincipalDetails(member);
    }
}
