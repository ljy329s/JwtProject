package com.example.jwtproject.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
import java.io.IOException;
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
     * 엑세스 토큰을 생성하는 메서드
     */
    
    @Transactional
    public String createToken(String username) {
        System.out.println("엑세스토큰생성");
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
    public void refreshToken(String username) {//PrincipalDetails principal
        String refKey = username;
        String refToken = JWT.create()
            .withSubject("Jwt_refreshToken")
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getRefreshTime()))//만료시간 6분
            .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        redisService.setRefreshToken(refKey, refToken, jwtYml.getRefreshTime());
        log.info("RefToken 생성 : " + refToken);
    }
    
    /**
     * 엑세스토큰 만료여부를 확인하는 메서드
     */
    
    public boolean isExpiredAccToken(String jwtToken) {
        String npToken = jwtToken.replace(jwtYml.getPrefix(), "");//헤더제거
        
        Date now = new Date();
        try {
            Date expiresAt = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(npToken)
                .getExpiresAt();
            log.info("지금시간 : " + now);
            log.info("엑세스토큰의 만료시간 : " + expiresAt);
            if (now.before(expiresAt)) {//현재시간이 만료시간보다 이전이라면
                System.out.println("만료전");
                return false;
            }
        } catch (TokenExpiredException e) {
            log.info("만료된 토큰입니다.");
            return true;
        }
        return false;
        
    }
    
    /**
     * 리프레시 토큰의 만료여부를 확인하는 토큰 레디스에 존재하면 아직 만료기간 안지난거니까
     */
    
    public void isExpiredRefToken(String username, HttpServletResponse response) {
        System.out.println("리프레시 토큰의 만료여부를 확인하는 메서드 isExpiredRefToken()");
        try {
            if (redisService.getRefreshToken(username) != null) {//리프레시 토큰이 존재한다면
                System.out.println("리프레시 토큰 존재 만료기간 확인");
                if (checkRefreshToken(username)) {//리프레시 토큰의 만료기간 확인 7일전이라면
                    log.info("리프레시 토큰 만료 7일전 리프레시토큰 재생성");
                    refreshToken(username);//리프레시 토큰 생성
                    createToken(username);//엑세스토큰 생성
                } else {
                    log.info("리프레시토큰 만료일 7일이상 엑세스토큰만 재발급");
                    createToken(username);
                }
            } else {
                log.info("리프레시 토큰이 없습니당 재로그인 요청1");
                response.sendRedirect("/logout");
                
            }
        } catch (TokenExpiredException | IOException e) {
            System.out.println(e.getMessage());
            log.info("리프레시 토큰이 없습니당 재로그인 요청2");
        }
    }
    
    /**
     * 리프레시 토큰의 만료기간을 확인하는 메서드 7일이내 만료 여부
     */
    
    public boolean checkRefreshToken(String username) {
        try {
            
            String token = redisService.getRefreshToken(username);
            Date expiresAt = JWT.require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(token)//verify는 만료된 토큰을 예외로 던져준다.
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
        } else {//쿠키가 존재하지 않는다면
            System.out.println("쿠키없음");
            return null;
        }
        return null;
    }
    
    
    /**
     * token 을 디코드 하여 username 을 반환하는 메서드
     */
    
    public String getNameFromToken(String token) {
        
        String npToken = token.replace(jwtYml.getPrefix(), "");
        if (token != null) {
            DecodedJWT decodedJwt = JWT.decode(npToken);//decode() 메서드는 토큰의 만료를 확인하지 않으므로 토큰이 만료된 경우에도 사용자 이름을 반환한다.
            String username = decodedJwt.getClaim("username").asString();
            return username;
        } else {
            return null;
        }
    }
}
    

