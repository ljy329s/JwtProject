package com.example.jwtproject.model.controller;

import com.example.jwtproject.model.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class MainController {
    
    private final RedisService redisService;
    
    @RequestMapping(method = {RequestMethod.GET,RequestMethod.POST},value="/")
    public String Main() {
        return "<h1>메인화면입니다</h1>";
    }
    
    @GetMapping(value = "/test")
    public String Test() {
        return "<h1>테스트화면입니다</h1>";
    }
    
    @GetMapping("/member/loginForm")
    public Map<String, String> loginForm() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "로그인폼이동");
        //로그인폼으로 이동하기
        return result;
    }
    
    @PostMapping("/member/failLoginForm")
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
        System.out.println("==========레디스에서 유저정보 조회===========");
        String data1 = redisService.getUserDate(data.get("username"), data.get("filed"));
        System.out.println("=== userData ===" + data1);
    }
    
    /**
     * 레디스에서 user의 권한확인
     */
    @PostMapping("/selectUserRoles")
    public void selectUserRoles(@RequestBody Map<String,String> data) {
        System.out.println("==========레디스에서 유저정보 조회===========");
        
        String data1 = redisService.getUseRole(data.get("username"));
        System.out.println("=== userRole === " + data1);
        
    }
}
