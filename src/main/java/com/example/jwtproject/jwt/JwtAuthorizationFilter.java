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
 * 권한이나 인증이 필요없다면 타지않게된다.
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
    
    //인증이나 권한이 필요한 요청에는 이 필터를 거치게 된다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //모든요청시 거치는 doFilterInternal에서 제일먼저 엑세스토큰의 만료여부를 체크하는 로직을 돌게함
        //acToken : prefix가 포함된 엑세스토큰
        
        /**
         엑세스 토큰이 담겨있는 쿠키의 존재여부를 확인
         */
        log.info("쿠키의 존재여부를 가장먼저 체크한다");
       
        //쿠키가 없을때
        String acToken = "";
        acToken = tokenProvider.getTokenFromCookie(request, response);
        if (acToken == null) { //쿠키자체가 없으면 로그인이 안된거니까 메인화면으로 이동
            System.out.println("엑세스토큰이 담긴 쿠키가 없음");
            response.sendRedirect("/");
            return;
        }
        String username = tokenProvider.getNameFromToken(acToken);//토큰에서 이름 꺼내기
        log.info("쿠키가 존재한다! 모든요청마다 엑세스토큰의 만료여부를 가장 먼저 체크한다");
        
        //엑세스토큰이 담긴 쿠키가 있으면서 엑세스 토큰이 만료가 아니라면 동작
        if (!tokenProvider.isExpiredAccToken(acToken)) {//엑세스토큰 만료가 아니라면
            System.out.println("acToken : " + acToken);
        }
        
        if (tokenProvider.isExpiredAccToken(acToken)) {//엑세스토큰 만료라면
            log.info("========= 엑세스 토큰 만료! 리프레시 토큰의 만료여부 확인 =========");
            boolean refEx = tokenProvider.isExpiredRefToken(username, response);//리프레시 토큰의 존재여부
            
            if (!refEx) {//리프레시 토큰이 없다면
                response.sendRedirect("/member/loginForm");
                return;
            }
        }
        
        log.info("=============== 인증여부 확인 ===============");
        
        log.info("1.쿠키에서 꺼낸 토큰 검증");
        String accToken = tokenProvider.getTokenFromCookie(request, response);
        //헤더가 비어있거나 Bearer 방식이 아니라면 반환시킨다.
        if (accToken == null || !accToken.startsWith(jwtYml.getPrefix())) {
            chain.doFilter(request, response);
            log.info("권한이 없습니다");
            return;
        }
        
        //정상적인 사용자인지, 권한이 있는지 확인
        log.info("2. username 으로 회원을 조회한다.");
        Member member = memberRepository.selectMember(username);
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        
        System.out.println("3. jwt 토큰서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        System.out.println("4. SecurityContextHolder에 Authentication 객체를 저장해서 인가 양도처리.");
        //sequrityContextHolder에 전달받은 jwt로 만든 authentication 을 저장해준다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        chain.doFilter(request, response);
    }
    
}


