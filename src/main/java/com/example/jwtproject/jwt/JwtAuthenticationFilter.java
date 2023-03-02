package com.example.jwtproject.jwt;

import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.domain.Login;
import com.example.jwtproject.model.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
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
        
        log.info("=========로그인시도시 쿠키 삭제=========");//쿠키삭제를 해주지 않으니까 로그인을 실패해도 넘어가버린당..ㅠ
        Cookie delCookie = new Cookie("Authorization",null);
        delCookie.setMaxAge(0);
        response.addCookie(delCookie);

        log.info("로그인시도");
        ObjectMapper om = new ObjectMapper();
        
        try {
            //유저가 입력한 username,password 를 받는다.
            //request 로 넘어오는 username, password 를 받아서 로그인요청 객체를 생성후
            //Authenticate 를 위한 UserPasswordAuthenticationToken 을 발행한다.
            
            Login login = om.readValue(request.getInputStream(), Login.class);
            
            //username, password를 이용해서 UsernamePasswordAuthenticationToken 발급
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
            
            log.info(authenticationToken.getPrincipal().toString());//username은 principal 이 되고
            log.info(authenticationToken.getCredentials().toString());//password는 credentials가 된다.
            
            
            // 2. 1번에서 전달받은 로그인정보를 이용해서 생성한 토큰을 가지고 로그인이 유효한지 검증
            // 회원조회후 존재할때 해당토큰 검증하면된다.(principal == username && credentials == password)
            // 패스워드를 비교하는 로직은 시큐리티 내부에서 검증하기에 따로 작성하지 않아도 된다.
            // id 와 pw가 일치하면 알아서 authentication 을 반환해주고 , 아니라면 연결종료시킴
            //authenticationManager클래스의 authenticate()에 토큰을 넘기면 자동으로
            //UserDetailsService.class 의 loadUserByUsername() 메소드가 실행된다.
            
            System.out.println("============== 로그인 검증 시작 ===============");
            Authentication authentication =
                authenticationManager.authenticate(authenticationToken);//authenticate(Authentication) : 인증의 전반적인 관리
            //3.로그인 성공 확인
            System.out.println("3. 로그인 성공여부 확인중");
            
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            
            return authentication;//authentication을 반환하면 세션에 저장된다.
            
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
        log.info("=========== 로그인 성공시 동작 : JwtAuthenticationFilter 의 successfulAuthentication() ===========");
        //---  super.successfulAuthentication(request, response, chain, authResult);
        
        PrincipalDetails principal = ((PrincipalDetails) authResult.getPrincipal());
        
        String username = principal.getUsername();
        
        String roleList = principal.getMember().getRoleList().toString();
        //레디스에 유저 권한 정보 보내기
        redisService.setUserRole(username, roleList, jwtYml.getAccessTime());
        redisService.setUserDate(username, principal, jwtYml.getAccessTime());
        
        //레디스에서 유저 권한 조회하기 임시로 여기에 작성
        //start
        String roles = redisService.getUseRole(username);
        String cleanRoles = roles.replaceAll("[ \\[ \\] ]", "");
        
        
        int count = cleanRoles.length() - cleanRoles.replace(",", "").length();//특정 문자의 갯수
        System.out.println("유저권한 조회" + cleanRoles);
        String[] d = cleanRoles.split(",");
        
        for (int i = 0; i <= count; i++) {
            System.out.println(",기준으로 " + d[i]);
        }
        //end
        //이렇게 조회한 권한 이름 등등의 정보를 레디스에 넣어야함
        String accToken = tokenProvider.createToken(username,response);//엑세스토큰 생성하는 메서드
        // 만약 권한이나 다른것들도 넣어줘야하면 principal을 넘겨주는것으로 변경하지
        
        tokenProvider.refreshToken(username); //리프레시 토큰 생성 맟 레디스 저장
        log.info("AccToken : " + "Bearer " + accToken);
        
    }
    
    
    /**
     * 로그인 실패시 호출되는 메서드
     * AuthenticationService 에서 발생하는 exception handling
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("=========== 로그인 실패시 동작 : JwtAuthenticationFilter 의 unsuccessfulAuthentication ===========");
        response.sendRedirect("/member/failLoginForm");
    }
}
