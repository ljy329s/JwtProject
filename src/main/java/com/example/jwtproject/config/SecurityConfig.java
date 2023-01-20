package com.example.jwtproject.config;

import com.example.jwtproject.filter.AuthCustomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfig corsConfig;
    
    private final AuthCustomFilter authCustomFilter;
    
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        return http
                .httpBasic().disable()
                .addFilter(corsConfig.corsFilter())
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션사용안함
                .and()
                .formLogin().disable()
                .apply(authCustomFilter)
                .and()
                .authorizeRequests()
                .antMatchers("/test").access("hasRole('ROLE_USER')")
                .anyRequest().permitAll()//임시적 모든 권한 다 허용
                .and()
                .build();

    }
    
    
}
