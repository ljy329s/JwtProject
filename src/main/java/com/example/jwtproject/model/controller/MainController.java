package com.example.jwtproject.model.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;

@RestController
public class MainController {
    
    @GetMapping("/")
    public String Main(){
        return "<h1>메인화면입니다</h1>";
    }
    
    @GetMapping(value = "/test")
    public String Test(){
        return "<h1>테스트화면입니다</h1>";
    }
    
}
