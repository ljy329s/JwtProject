package com.example.jwtproject.config;

import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.filter.AuthCustomFilter;
import com.example.jwtproject.jwt.JwtAuthenticationEntryPoint;
import com.example.jwtproject.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    
    private final CorsConfig corsConfig;
    
    private final AuthCustomFilter authCustomFilter;
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                    .httpBasic().disable()
                    .addFilter(corsConfig.corsFilter())
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션사용안함
                .and()
                    .formLogin().disable()
                    .apply(authCustomFilter)
                .and()
                    .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                    .authorizeRequests()
                    .antMatchers("/test").authenticated()
                    .antMatchers("/", "/member/**", "/user/**", "/jyHome","/selectUserData","/selectUserRoles","/selectUserDB").permitAll()
                    .anyRequest().permitAll()//모든 권한 다 허용
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .deleteCookies("Authorization")
                .and()
            .build();

    }
    
    @Bean
    public WebSecurityCustomizer configure(){ // 시큐리티의 적용에서 제외할것들(필터를 타지 않게 하려는것들 등록)
        return (web) -> web.ignoring().mvcMatchers(
            "/","/member/failLoginForm","/member/loginForm"
        );
    }
}
