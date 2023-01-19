package com.example.jwtproject.jwt;

import com.example.jwtproject.auth.PrincipalDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider {
    
    /**
     * 엑세스토큰 만료시간 : 1분
     */
    private long accessTokenValidTime = Duration.ofMinutes(1).toMillis();//만료시간 30분
    
    /**
     * //     * 리프레시토큰 만료시간 : 3분
     * //
     */
    private long refreshTokenValidTime = Duration.ofMinutes(3).toMillis();
    
    @Value("${jwt.secret-key}")
    private String secretKey;
    
    //초기화 secretKey를 base64로 인코딩한다.
//    @PostConstruct
//    protected void init(String secret) {
//        String secretKey = Base64.getEncoder().encodeToString(secret.getBytes());
//        System.out.println(secretKey);
//    }
    
    //토큰 생성 메서드
   
    public String createToken(PrincipalDetails principalDetails , String secretKey) {
        System.out.println("TokenProvider createToken 시작");
    
        //만료시간설정
        long tokenValidTime = Duration.ofMinutes(1).toMillis();
        Date now = new Date();
    
        //header 설정
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");
    
        //payload 설정
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("id", principalDetails.getMember().getId());//db는 int domain은 String 으로 지정
        payloads.put("email", principalDetails.getMember().getEmail());
        System.out.println(secretKey);
    
        String token = Jwts.builder()
            .setSubject("jwtTest")
            .setIssuer("ljy")//토큰발급자
            .setHeader(headers)
            .setClaims(payloads)//정보
            .setIssuedAt(now)//토큰발행시간
            .setExpiration(new Date(now.getTime() + tokenValidTime))//토큰만료시간
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();// 토큰발행
        return token;
  
    }
//        return Token.builder()
//            .key(principalDetails.getMember().getEmail())
//            .value(token)
//            .expiredTime(tokenValidTime)
//            .build();
//    }
////
//    public Token createToken(Member member) {
//        System.out.println("TokenProvider createToken 시작");
//
//        //만료시간설정
//        long tokenValidTime = accessTokenValidTime;
//        Date now = new Date();
//
//        //header 설정
//        Map<String, Object> headers = new HashMap<>();
//        headers.put("typ", "JWT");
//        headers.put("alg", "HS256");
//
//        //payload 설정
//        Map<String, Object> payloads = new HashMap<>();
//        payloads.put("id",member.getId());//db는 int domain은 String 으로 지정
//        payloads.put("email",member.getEmail());
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
//
//        System.out.println("token : " + token);
//
//        return Token.builder()
//            .key(member.getEmail())
//            .value(token)
//            .expiredTime(tokenValidTime)
//            .build();
//    }
//    public Token createToken(Member member) {
//        System.out.println("TokenProvider createToken 시작");
//
//        long tokenValidTime = accessTokenValidTime;
//        Date now = new Date();
//
//        Claims claims = Jwts.claims().setSubject(member.getEmail());//sub은 토큰 용도 ? 제목?
//        claims.put("roles", member.getRoles());
//        claims.put("email", member.getEmail());
//        System.out.println("claims" + claims);
//        //header 설정
//        //payload 설정
//
//        String token = Jwts.builder()
//            .setIssuer("ljy")//토큰발급자
//            .setClaims(claims)//정보
//            .setIssuedAt(now)//토큰발행시간
//            .setExpiration(new Date(now.getTime() + tokenValidTime))//토큰만료시간
//            .signWith(SignatureAlgorithm.HS256, secretKey)
//            .compact();// 토큰발행
//        System.out.println("claim: " + claims);
//        System.out.println("token : " + token);
//
//        return Token.builder()
//            .key(member.getEmail())
//            .value(token)
//            .expiredTime(tokenValidTime)
//            .build();
//    }
    
}

