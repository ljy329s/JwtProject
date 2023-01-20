package com.example.jwtproject.jwt;


import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.domain.Member;
import com.example.jwtproject.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 토큰의 검증, 발급을 담당할 클래스
 */
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtYml jwtYml;
    private final MemberRepository memberRepository;
    
//    public Member getMemberRefreshToken(String token){
//        return memberRepository.selectRefreshToken(token);
//    }
//
//    public void setUpdateRefreshToken(String username, String refreshJwt){
//        memberRepository.selectMember(username)l
//    }
//
//
}
