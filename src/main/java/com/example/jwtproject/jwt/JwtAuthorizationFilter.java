package com.example.jwtproject.jwt;

import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.domain.Member;
import com.example.jwtproject.model.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 권한이나 인증이 필요한 특정 주소를 요청했을때 BasicAuthenticationFilter 를 타게된다.
 */

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final JwtYml jwtYml;
    
    private final MemberRepository memberRepository;
    
    private final TokenProvider tokenProvider;
    
    
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, JwtYml jwtYml, TokenProvider tokenProvider) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtYml = jwtYml;
        this.tokenProvider = tokenProvider;
    }
    
    //모든요청시 거치는 doFilterInternal에서 제일먼저 엑세스토큰의 만료여부를 체크하는 로직 생성함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        /**
         엑세스 토큰이 담겨있는 쿠키의 존재여부를 확인
         */
        
        log.info("쿠키의 존재여부 확인");
        
        /**
         * 쿠키가 없을때 동작 쿠키가 없으면 종료
         */
        String acToken = "";
        acToken = tokenProvider.getTokenFromCookie(request);
        if (acToken == null) { //쿠키자체가 없으면 로그인이 안된거니까 메인화면으로 이동
            log.info("엑세스토큰이 담긴 쿠키가 없음");
            response.sendRedirect("/");
            return;
        }
        
        /**
         * 쿠키가 있을때 동작 모든요청마다 엑세스토큰의 만료여부를 가장 먼저 체크한다
         */
        String username = tokenProvider.getNameFromToken(acToken);//토큰에서 이름 꺼내기
        
        //엑세스 토큰이 만료가 아니라면 동작
        if (!tokenProvider.isExpiredAccToken(acToken)) {
            System.out.println("acToken : " + acToken);
        }
        
        //엑세스 토큰이 만료라면 동작
        if (tokenProvider.isExpiredAccToken(acToken)) {
            log.info("========= 엑세스 토큰 만료! 리프레시 토큰의 만료여부 확인 =========");
            boolean refEx = tokenProvider.isExpiredRefToken(username, response);//리프레시 토큰의 존재여부
            
            //리프레시 토큰이 만료라면
            if (!refEx) {
                response.sendRedirect("/member/loginForm");
                return;
            }
        }
        
        log.info("=============== 쿠키에서 토큰을 꺼내서 검증 시작 ===============");
        
        String accToken = tokenProvider.getTokenFromCookie(request);
        //헤더가 비어있거나 Bearer 방식이 아니라면 반환시킨다.
        if (accToken == null || !accToken.startsWith(jwtYml.getPrefix())) {
            chain.doFilter(request, response);
            log.info("권한이 없습니다");
            return;
        }
        
        //정상적인 사용자인지, 권한이 있는지 확인
        Member member = memberRepository.selectMember(username);
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        chain.doFilter(request, response);
    }
}


