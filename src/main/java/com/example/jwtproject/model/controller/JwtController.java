package com.example.jwtproject.model.controller;

import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.jwt.TokenProvider;
import com.example.jwtproject.model.domain.Token;
import com.example.jwtproject.model.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtYml jwtYml;
    private final TokenProvider tokenProvider;
    //리프레시 토큰을 통해서 엑세스토큰 재발급 요청
    
    //프론트에서 모든 요청전 항상 프론트에서 엑세스토큰의 만료여부를 확인하고 만료됐다면 reissue를 통해서 재발급 처리를 해야함
    @PostMapping("/reissue")
    public void reissue(@RequestBody Token username , HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        System.out.println("reissue 접근 username: " + username );

        String key = username.getUsername();
        System.out.println("key: "+key);
        if(tokenProvider.isExpiredRefToken(key)){
            System.out.println("리프레시 토큰 존재 엑세스토큰 재발급 하기");
           String jwtToken = request.getHeader(jwtYml.getHeader()).replace(jwtYml.getPrefix() + " ", "");
           String accToken = tokenProvider.reCreateAccToken(jwtToken);

            System.out.println("엑세스토큰 재발급: " +"Bearer "+ accToken);
           response.setHeader("Authorization", "Bearer " + accToken);
//
        }else {
            System.out.println("리프레시 토큰 없음 로그아웃하기");
        }

    }
}
