package com.example.jwtproject.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
public class JwtYml {
    
    private final String header;
    
    private final String secretKey;
    
    private final String prefix;
    
}
