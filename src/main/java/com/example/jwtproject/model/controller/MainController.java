package com.example.jwtproject.model.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {
    
    @GetMapping("/")
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
    
    @GetMapping("/member/failLoginForm")
    public Map<String, String> failLoginForm(HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        result.put("result", "fail");
        result.put("message", "로그인실패");
        //로그인폼으로 이동하기
        return result;
    }
    
}
