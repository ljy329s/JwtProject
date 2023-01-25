package com.example.jwtproject.handler;


import com.example.jwtproject.auth.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 동적인 인가 처리를 위한 핸들러
 */
@Component
public class AuthorizationDynamicHandler{
    
    public boolean isAuthorization(HttpServletRequest request, Authentication authentication){
        System.out.println("요청 url : " + request.getRequestURI());
    
        Object principalDetails = (PrincipalDetails)authentication.getPrincipal();
        
        if (principalDetails == null || (principalDetails instanceof UserDetailsService)){
            System.out.println("");
        }return true;
    }
}
