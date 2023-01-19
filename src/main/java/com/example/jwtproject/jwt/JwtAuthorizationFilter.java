//package com.example.jwtproject.jwt;
//
//import com.example.jwtproject.model.repository.MemberRepository;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * 권한이나 인증이 필요한 특정 주소를 요청했을때 BasicAuthenticationFilter를 타게된다.
// * 권한이나 인증이 필요없다면 타지않게된다.
// */
//
//public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
//
//    private MemberRepository memberRepository;
//
//    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
//        super(authenticationManager);
//        this.memberRepository = memberRepository;
//    }
//
//    //인증이나 권한이 필요한 요청에는 이 필터를 거치게 된다.
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        System.out.println("===============인증 및 권한 확인하는 필터 접속===============");
//        System.out.println("1.권한이나 인증이 필요한 요청이 전달됨!");
//
//        String jwtHeader = request.getHeader("Authorization");//헤더에 들어있는 Authorization을 꺼낸다.
//        System.out.println("jwtHeader : "+ jwtHeader);
//
//        System.out.println("2.Header 검증");
//        //헤더가 비어있거나 Bearer방식이 아니라면 반환시킨다.
//        if(jwtHeader == null || !jwtHeader.startsWith("Bearer")){
//            chain.doFilter(request,response);
//            return;
//        }
//        System.out.println("=========================");
//
//        //정상적인 사용자인지, 권한이 있는지 확인
//        System.out.println("3. Jwt 토큰을 검증해서 정상적인 사용자인지, 권한이 맞는지 확인");
//        String jwtToken = request.getHeader("Authorization").replace("Bearer ","");
//        String username = null;
//
//
//
//
//    }
//
//
//}
