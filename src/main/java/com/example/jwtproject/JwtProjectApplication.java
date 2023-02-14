package com.example.jwtproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class JwtProjectApplication {
    

    public static void main(String[] args) {
        SpringApplication.run(JwtProjectApplication.class, args);
    }
    
}
