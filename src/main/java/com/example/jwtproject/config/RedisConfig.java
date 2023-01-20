package com.example.jwtproject.config;


import com.example.jwtproject.common.RedisYml;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
public class RedisConfig {
    
    private final RedisYml redisYml;
    
    private final RedisProperties redisProperties;
    
    /**
     * RedisConnectionFactory
     * redis서버와의 통신을 위한 low-level 추상화를 제공
     */
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisYml.getHost(),redisYml.getPort());
    }
    
    
    /**
     * 저장된 키와 값 읽기
     * StringRedisSerializer 을 이용해서 저장된 키와 값을 읽읅 수 있다.
     */
    @Bean
    public RedisTemplate<String, Object> RedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        
        return redisTemplate;
    }
}
