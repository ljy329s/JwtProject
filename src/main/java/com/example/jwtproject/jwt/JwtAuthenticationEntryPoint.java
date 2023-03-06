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
        
    }
}
