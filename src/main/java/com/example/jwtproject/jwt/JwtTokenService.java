package com.example.jwtproject.jwt;


import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtYml jwtYml;
    private final MemberRepository memberRepository;
    
    
}
