package com.example.jwtproject.config;

import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.filter.AuthCustomFilter;
import com.example.jwtproject.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
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
        System.out.println("시큐리티 컨피그");

        return http
                .httpBasic().disable()
                .addFilter(corsConfig.corsFilter())
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션사용안함
                .and()
                .formLogin().disable()
                .apply(authCustomFilter)
                .and()
//            이곳에 동적으로 뿌려주는걸 해야함 예정!!
                .authorizeRequests()
                .antMatchers("/test").access("hasRole('ROLE_USER')")
                .antMatchers("/","/reissue").permitAll()
                //.anyRequest().authenticated()// 나머지 요청은 인증된 사람만 허용가능
                .anyRequest().permitAll()//모든 권한 다 허용
                .and()
                .build();

    }
    
}
