package com.example.jwtproject.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Base64;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
public class JwtYml {
    
    private final String header;
    
    private final String secretKey;
    
    private final String prefix;
    
    private final long accessTime;
    
    private final long refreshToken;
    
    public String getSecretKey() {
        String secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
        return secretKey;
    }
}
