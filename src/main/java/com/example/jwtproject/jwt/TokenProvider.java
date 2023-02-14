package com.example.jwtproject.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

import static com.auth0.jwt.JWT.require;

/**
 * 토큰과 관련된 작업을 하는 클래스
 */

@Slf4j
@Component
@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtYml jwtYml;
    
    private final RedisService redisService;
    
    
    /**
     * 엑세스 토큰을 만드는 메서드
     */
    
    @Transactional
    public String createToken(String username) {
        String token = JWT.create()
            .withSubject("Jwt_accessToken")
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getAccessTime()))//만료시간 2분
            .withClaim("username", username)
            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        return token;
    }
    
    /**
     * 리프레시 토큰을 생성하는 메서드
     */
    @Transactional
    public String refreshToken(PrincipalDetails principal) {
        String refKey = principal.getMember().getUsername();
        String refToken = JWT.create()
            .withSubject("Jwt_refreshToken")
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getRefreshTime()))//만료시간 6분
            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        redisService.setRefreshToken(refKey, refToken);
        System.out.println("리프레시 토큰 발행 : " + refToken);
        return null;
    }
    
    
    /**
     * 엑세스 토큰을 재발급 하는 메서드
     */
    
    @Transactional
    public String reCreateAccToken(String jwtToken) {
        
        String username = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
            .build()
            .verify(jwtToken)
            .getClaim("username")
            .asString();//1. 만료된 엑세스토큰에서 username 아이디를 가져온다
        
        String reAccToken = createToken(username);// 2. 엑세스토큰 재발급 기존의 엑세스토큰의 발행메소드 사용
        return reAccToken;
    }
    
    
    /**
     * 리프레시 토큰을 재발행 하는 메서드
     */
    
    @Transactional
    public String refreshUpdateToken(String username) {
            String refToken = JWT.create()
                .withSubject("Jwt_refreshToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getRefreshTime()))//만료시간 6분
                .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
            redisService.setRefreshToken(username, refToken);
            System.out.println("리프레시 토큰 재발행 : " + refToken);
        return null;
        
    }
    
    /**
     * 엑세스토큰 만료여부를 확인하는 메서드
     */
    
    public boolean isExpiredAccToken(String jwtToken) {
    
        Date now = new Date();
        try {
            Date expiresAt = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(jwtToken)
                .getExpiresAt();
            System.out.println("지금시간" + now);
            System.out.println("엑세스토큰의 만료여부 확인" + expiresAt);
            if (now.before(expiresAt)) {//현재시간이 만료시간보다 이전이라면
                System.out.println("만료전");
                return false;
            }
        } catch (TokenExpiredException e) {
            System.out.println("만료된 토큰입니다.");
        } finally {
            return true;
        }
    }
    
    /**
     * 리프레시 토큰의 만료여부를 확인하는 토큰 레디스에 존재하면 아직 만료기간 안지난거니까
     */
    
    public boolean isExpiredRefToken(String username) {
        try {
            
            if (redisService.getRefreshToken(username) != null) {//리프레시 토큰이 존재한다면
                if (checkRefreshToken(username)) {//리프레시 토큰의 만료기간 확인 7일전이라면
                    log.info("리프레시 토큰 7일전");
                    System.out.println("리프레시 토큰 만료 7일전 리프레시토큰 재생성");
                    refreshUpdateToken(username);//리프레시 토큰 생성
                }
                return true;
            }
        } catch (TokenExpiredException e) {
            System.out.println(e.getMessage());
            log.info("리프레시 토큰이 없습니당");
            System.out.println("리프레시토큰이 없습니다 로그아웃처리");
            
        }
        return false;
    }
    
    /**
     * 리프레시 토큰의 만료기간을 확인하는 메서드 7일이내 만료 여부
     */
    
    public boolean checkRefreshToken(String username) {
        try {
            
            String token = redisService.getRefreshToken(username);
            Date expiresAt = JWT.require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(token)
                .getExpiresAt();
            
            Date current = new Date(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.add(Calendar.MILLISECOND, 120000);//임시 2분.  7일 남았을때로 해주기 변경 Calendar.DATE
            
            Date after7dayFromToday = calendar.getTime();
            
            System.out.println("리프레시 만료 기간" + expiresAt);
            
            //7일이내 만료
            if (expiresAt.before(after7dayFromToday)) {
                System.out.println("리프레시토큰 7일이내 만료");
                return true;
            }
        } catch (TokenExpiredException e) {
            return true;
        }
        return false;
        
    }
    
    /**
     * 쿠키에서 토큰을 꺼내는 메서드 쿠키에 저장된 엑세스토큰을 리턴한다
     */
    public String getTokenFromCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {//쿠키가 존재한다면
            for (Cookie c : cookies) {//쿠키를 꺼내는데
                if (c.getName().equals("Authorization") && c != null) {//쿠키중에 이름이  Authorization인것만 가져오기 + 비어있지 않을때
                    String accToken = c.getValue();//쿠키에 저장된 엑세스토큰
                    return accToken;
                }
            }
            
        }//쿠키가 존재하지 않는다면
        else {
            System.out.println("쿠키없음");
            return null;
        }
        return null;
    }
    
    
    /**
     * token을 디코드 하여 username을 반환하는 메서드
     */
    public String getNameFromToken(String token) {
        String npToken = token.replace(jwtYml.getPrefix(),"");
        try {
            String username = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(npToken)//prefix가 없는 토큰
                .getClaim("username")
                .asString();
    
            System.out.println("username"+username);
    
            return username;
        } catch (TokenExpiredException e) {
            return null;
        }
    }
}
