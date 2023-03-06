package com.example.jwtproject.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인가가 실패했을때 실행된다. (로그인하지 않은 사용자가 접근)
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        
        log.info("JwtAuthenticationEntryPoint 의 commence ==> 예외발생");
        response.setCharacterEncoding("utf-8");
        response.sendError(401, "잘못된 접근입니다.");
    
    
        /**
         * JWT Exception 종류
         * ExpiredJwtException : JWT를 생성할 때 지정한 유효기간 초과할 때.
         * UnsupportedJwtException : 예상하는 형식과 일치하지 않는 특정 형식이나 구성의 JWT일 때
         * MalformedJwtException : JWT가 올바르게 구성되지 않았을 때
         * SignatureException :  JWT의 기존 서명을 확인하지 못했을 때
         */
    }
}
