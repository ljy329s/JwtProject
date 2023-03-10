package com.example.jwtproject.jwt;

import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.domain.Login;
import com.example.jwtproject.model.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 로그인 요청이 오면 JwtAuthenticationFilter 에서 attemptAuthentication() 을 호출하여 인증처리
 * username , password가 정상이면 authenticationToken을 발급
 * - 인증토큰 객체를 이용해서 userDetailsService 의 loadByUserName()을 호출하여 회원 존재여부를 검증
 * - UserDetails 객체로 반환된 객체에 대해서 password Encoder 통해서 패스워드를 검증
 */

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter implements Serializable {//UsernamePasswordAuthenticationFilter 로그인 인증을 처리하는 필터
    
    private final AuthenticationManager authenticationManager;
    
    private final JwtYml jwtYml;
    
    private final TokenProvider tokenProvider;
    
    private final RedisService redisService;
    
    static final long serialVersionUID = 1L;
    
    
    /**
     * /login 요청 하면 로그인 시도를 위해서 실행되는 메소드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        
        log.info("=========로그인시도 쿠키 삭제=========");
        Cookie delCookie = new Cookie("Authorization", null);
        delCookie.setMaxAge(0);
        response.addCookie(delCookie);
        
        ObjectMapper om = new ObjectMapper();
        
        try {
            Login login = om.readValue(request.getInputStream(), Login.class);
            
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
            
            // 전달받은 로그인 정보를 이용해서 생성한 토큰 authenticationToken을 가지고
            // 회원조회후 존재하는 회원일때 해당토큰 검증(아이디 비밀번호 일치 : principal == username && credentials == password)
            // authenticationManager.authenticate()에 토큰을 넘기면 자동으로 UserDetailsService.class의 loadUserByUsername() 메소드가 실행
            Authentication authentication;
            try {
                authentication =
                    authenticationManager.authenticate(authenticationToken);//authenticate(Authentication) : 인증의 전반적인 관리
            } catch (NullPointerException | InternalAuthenticationServiceException e) {
                log.info("해당유저가 없습니다.");
                return null;
            }
            return authentication;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    /**
     * 성공시!
     * attemptAuthentication() 실행후 인증이 정상완료되면 실행된다.
     * 따라서 , 여기서 jwt 토큰을 만들어서 request 요청한 사용자에게 jwt 토큰을 response 해준다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        
        PrincipalDetails principal = ((PrincipalDetails) authResult.getPrincipal());
        String username = principal.getUsername();
        String roleList = principal.getMember().getRoleList().toString();
        
        //레디스에 유저 권한 정보 보내기
        redisService.setUserRole(username, roleList, jwtYml.getRefreshTime());
        redisService.setUserDate(username, principal, jwtYml.getRefreshTime());
        
        //토큰 생성
        tokenProvider.createToken(username, response);
        tokenProvider.refreshToken(username);
        response.sendRedirect("/jyHome");
    }
    
    /**
     * 로그인 실패시 호출되는 메서드
     * AuthenticationService 에서 발생하는 exception handling
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("=========== 로그인 실패 ===========");
        response.sendRedirect("/member/failLoginForm");
    }
}
