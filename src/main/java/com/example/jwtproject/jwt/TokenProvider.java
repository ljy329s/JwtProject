package com.example.jwtproject.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

import static com.auth0.jwt.JWT.require;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final JwtYml jwtYml;

    private final RedisService redisService;
    /**
     * 엑세스토큰 만료시간 : 1분
     */
    private long accessTokenValidTime = Duration.ofMinutes(1).toMillis();//만료시간 30분

    /**
     * //     * 리프레시토큰 만료시간 : 3분
     * //
     */
    private long refreshTokenValidTime = Duration.ofMinutes(3).toMillis();


    /**
     * 엑세스 토큰을 만드는 메서드
     */
    public String createToken(PrincipalDetails principal) {
        String token = JWT.create()
                .withSubject("Jwt_accessToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getAccessTime()))//만료시간 2분
                .withClaim("username", principal.getMember().getUsername())
                .withClaim("roles", principal.getMember().getRoles())
                .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        return token;

    }

    /**
     * 엑세스 토큰을 재발급 하는 메서드
     */
    public String reCreateAccToken(String jwtToken) {
        System.out.println("엑세스토큰 재발급 메서드");


        String username = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(jwtToken)
                .getClaim("username")
                .asString();
        String roles = require(Algorithm.HMAC256(jwtYml.getSecretKey()))
                .build()
                .verify(jwtToken)
                .getClaim("roles")
                .asString();

        String reAcctoken = JWT.create()
                .withSubject("Jwt_accessToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getAccessTime()))//만료시간 2분
                .withClaim("username", username)
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        return reAcctoken;

    }

    /**
     * 리프레시 토큰을 생성하는 메서드
     */
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
     * 리프레시 토큰을 재발행는 메서드
     */
    public String refreshUpdateToken(String username) {
        String refToken = JWT.create()
                .withSubject("Jwt_refreshToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtYml.getRefreshTime()))//만료시간 6분
                .sign(Algorithm.HMAC256(jwtYml.getSecretKey()));
        redisService.setRefreshToken(username, refToken);
        System.out.println("리프레시 토큰 발행 : " + refToken);
        return null;

    }

    /**
     * 엑세스토큰 만료여부를 확인하는 메서드 이건 프론트에서 직접 해결할듯?
     */
    public boolean isExpiredAccToken(String username) {

        try {
            JWT.require(Algorithm.HMAC256(jwtYml.getSecretKey())).build().verify(username);
        } catch (TokenExpiredException e) {
            System.out.println("엑세스 토큰만료");
            return true;
        }
        return false;
    }

    /**
     * 리프레시 토큰의 만료여부를 확인하는 토큰
     */
    public boolean isExpiredRefToken(String username) {

        if (redisService.getRefreshToken(username) != null) {//리프레시 토큰이 존재한다면
            if (checkRefreshToken(username)) {//리프레시 토큰의 만료기간 확인 7일전이라면
                System.out.println("리프레시 토큰 만료 7일전 리프레시토큰 재생성");
                refreshUpdateToken(username);//리프레시 토큰 생성
            }

        return true;
        }
        System.out.println("리프레시토큰이 없습니다 로그아웃처리");
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
            calendar.add(Calendar.MILLISECOND, 120000);//임시 2분.  7일로 변경 Calendar.DATE , 7

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
}
