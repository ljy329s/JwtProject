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
    
    //토큰 생성 메서드
    
//    public String createToken(PrincipalDetails principalDetails, String secretKey) {
//        System.out.println("TokenProvider createToken 시작");
//
//        //만료시간설정
//        long tokenValidTime = Duration.ofMinutes(1).toMillis();
//        Date now = new Date();
//
//        //header 설정
//        Map<String, Object> headers = new HashMap<>();
//        headers.put("typ", "JWT");
//        headers.put("alg", "HS256");
//
//        //payload 설정
//        Map<String, Object> payloads = new HashMap<>();
//        payloads.put("id", principalDetails.getMember().getId());//db는 int domain은 String 으로 지정
//        payloads.put("email", principalDetails.getMember().getEmail());
//        System.out.println(secretKey);
//
//        String token = Jwts.builder()
//            .setSubject("jwtTest")
//            .setIssuer("ljy")//토큰발급자
//            .setHeader(headers)
//            .setClaims(payloads)//정보
//            .setIssuedAt(now)//토큰발행시간
//            .setExpiration(new Date(now.getTime() + tokenValidTime))//토큰만료시간
//            .signWith(SignatureAlgorithm.HS256, secretKey)
//            .compact();// 토큰발행
//        return token;
//
//    }

}

