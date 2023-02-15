package com.example.jwtproject.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.domain.Login;
import com.example.jwtproject.model.domain.Member;
import com.example.jwtproject.model.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.auth0.jwt.JWT.require;

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
        //모든요청시 거치는 doFilterInternal에서 제일먼저 엑세스토큰의 만료여부를 체크하는 로직을 돌게하기
        String acToken = tokenProvider.getTokenFromCookie(request, response);//prefix가 포함된 엑세스토큰
  
        log.info("모든요청시 엑세스토큰의 만료여부를 가장 먼저 체크한다");
        if (acToken != null) {//엑세스토큰이 있다면
            
            if (tokenProvider.isExpiredAccToken(acToken)) {//만료확인 만료일때 동작, 아니라면 건너뜀
                //만료일때만 동작
                System.out.println("엑세스 토큰 만료");
                String username = tokenProvider.getNameFromToken(acToken);//토큰에서 이름 꺼내기
                System.out.println("username ? 이걸가지고 리프레시 토큰의 만료 확인" + username);
    
                tokenProvider.isExpiredRefToken(username);//username을 통해서 리프레시 토큰의 만료여부를 확인
                
            }
            //엑세스 토큰이 있으며 만료가 되지 않았을때 동작
            String username = tokenProvider.getNameFromToken(acToken);
            System.out.println("username 확인 " + username);
            log.info("===============인증 및 권한 확인===============");
            System.out.println("1.권한이나 인증이 필요한 요청이 전달됨!");
            //쿠키에서 토큰을 가져온다.
            String accToken = tokenProvider.getTokenFromCookie(request, response);
            
            System.out.println("2.Header 검증");
            //헤더가 비어있거나 Bearer 방식이 아니라면 반환시킨다.
            if (accToken == null || !accToken.startsWith(jwtYml.getPrefix())) {
                chain.doFilter(request, response);
                System.out.println("권한이 없습니다");
                return;
            }
            
            //정상적인 사용자인지, 권한이 있는지 확인
            System.out.println("3. Jwt 토큰을 검증해서 정상적인 사용자인지, 권한이 맞는지 확인");
            String jwtToken;
            jwtToken = accToken.replace(jwtYml.getPrefix(), "");//prefix를 제외한 쿠키
            
            
            System.out.println("4. 정상적인 서명이 검증됐다. username으로 회원을 조회한다.");
            Member member = memberRepository.selectMember(username);
            PrincipalDetails principalDetails = new PrincipalDetails(member);
            
            System.out.println("5. jwt 토큰서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.");
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            
            System.out.println("6. 강제로 시큐리티 세션에 접근하여 Authentication 객체를 저장한다.");
            //sequrityContextHolder에 전달받은 jwt로 만든 authentication 을 저장해준다.
            //Authentication에는 현재 권한이 들어있으므로 권한이 필요한 곳에 조회할때 해당 권한을 체크해줄것
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            chain.doFilter(request, response);
            
        } else {
            response.sendRedirect("/");//쿠키자체가 없으면 로그인이 안된거니까 메인화면으로 이동
        }
        
    }
    
    
}
