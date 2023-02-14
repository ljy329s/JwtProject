package com.example.jwtproject.model.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/member")
public class MemberController {
    
    @GetMapping("/loginForm")
    public String loginForm() {
        return "/auth/loginForm";
    }
    
    @GetMapping("/failLogin")
    public String failLogin() {
        System.out.println("login 실패");
        return "/auth/loginForm";
    }
    
}
