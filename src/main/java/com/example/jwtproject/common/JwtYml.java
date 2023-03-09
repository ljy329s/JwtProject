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
    
    private final long refreshTime;
    
    private final int refreshAlarm;
    
    /**
     * secretKey를 get할때는 base64로 인코딩하고서 리턴
     */
    public String getSecretKey() {
        String secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
        return secretKey;
    }
}
