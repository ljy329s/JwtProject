package com.example.jwtproject.model.controller;

import com.example.jwtproject.jwt.TokenProvider;
import com.example.jwtproject.model.repository.MemberRepository;
import com.example.jwtproject.model.service.RedisService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class MainController {
    
    
    private final RedisService redisService;
    
    private final TokenProvider tokenProvider;
    
    private final MemberRepository memberRepository;
    
    
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/")
    public String Main() {
        return "<h1>jyHome 메인화면입니다</h1>";
    }
    
    @GetMapping(value = "/test")
    public String Test() {
        return "<h1>테스트화면입니다</h1>";
    }
    
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/member/loginForm")
    public Map<String, String> loginForm() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "로그인폼이동");
        return result;
    }
    
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/member/failLoginForm")
    public Map<String, String> failLoginForm(HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        result.put("result", "fail");
        result.put("message", "로그인실패");
        //로그인폼으로 이동하기
        return result;
    }
    
    /**
     * 레디스에서 user의 정보 확인
     */
    @PostMapping("/selectUserData")
    public void selectUserData(@RequestBody Map<String, String> data) {
        long beforeTime = System.currentTimeMillis(); //코드 실행 전의 시간
        System.out.println("==========레디스에서 유저정보 조회===========");
        String data1 = redisService.getUserDate(data.get("username"), data.get("filed"));
        System.out.println("=== userData ===" + data1);
        long afterTime = System.currentTimeMillis(); // 코드 실행 후의 시간
        long secDiffTime = (afterTime - beforeTime); //시간에 차 계산
        System.out.println("시간차이(m) : " + secDiffTime);
    }
    
    @PostMapping("/selectUserDB")
    public void selectUserDB(@RequestBody Map<String, String> data) {
        
        long beforeTime = System.currentTimeMillis(); //코드 실행 전의 시간
        System.out.println("==========DB에서 유저정보 조회===========");
        String username = data.get("username");
        String data1 = memberRepository.selectUserDB(username);
        System.out.println("=== userData ===" + data1);
        long afterTime = System.currentTimeMillis(); // 코드 실행 후의 시간
        long secDifTime = (afterTime - beforeTime); //시간 차 계산
        System.out.println("시간차이(m) : " + secDifTime);
    }
    
    /**
     * 레디스에서 user의 권한확인
     */
    @PostMapping("/selectUserRoles")
    public void selectUserRoles(@RequestBody Map<String, String> data) {
        System.out.println("==========레디스에서 유저정보 조회===========");
        
        List data1 = redisService.getUseRole(data.get("username"));
        System.out.println(" === userRole === " + data1);
        
    }
    
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/jyHome")
    public Map<String, String> jyHome() {
        Map<String, String> result = new HashMap<>();
        String token = tokenProvider.getTokenFromCookie();
        String username = null;
        if (token != null) {
             username = tokenProvider.getNameFromToken(token);
        }
        if (username != null) {
            result.put("username",username);
            result.put("result","success");
            return result;
        }
        result.put("username",null);
        result.put("result","fail");
        return result;
    }
}