package com.example.jwtproject.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.JwtYml;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final JwtYml jwtYml;
    
    /**
     * 엑세스토큰 만료시간 : 1분
     */
    private long accessTokenValidTime = Duration.ofMinutes(1).toMillis();//만료시간 30분
    
    /**
     * //     * 리프레시토큰 만료시간 : 3분
     * //
     */
    private long refreshTokenValidTime = Duration.ofMinutes(3).toMillis();
    
    
    //원본
    
    public String createToken(PrincipalDetails principal) {
        //JwtAuthenticationFilter 에 넣어뒀던 프로바이드
        String token = JWT.create()
            .withSubject("Jwt_accessToken")
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getAccessTime()))//만료시간 2분
            .withClaim("username", principal.getMember().getUsername())
            .withClaim("roles", principal.getMember().getRoles())
            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        return token;
    }
    
//    public String createToken(PrincipalDetails principal) {
//        //JwtAuthenticationFilter 에 넣어뒀던 프로바이드
//        if(createToken)
//        String token = JWT.create()
//            .withSubject("Jwt_accessToken")
//            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getAccessTime()))//만료시간 2분
//            .withClaim("username", principal.getMember().getUsername())
//            .withClaim("roles", principal.getMember().getRoles())
//            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
//        return token;
//    }
    
    


}

