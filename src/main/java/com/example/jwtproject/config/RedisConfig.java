package com.example.jwtproject.config;


import com.example.jwtproject.common.RedisYml;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    private final RedisYml redisYml;
    
    /**
     *
     * RedisTemplate 을 이용한 방식
     *
     * RedisConnectionFactory 인터페이스를 통해(redis 서버와의 통신을 위한 low-level 추상화를 제공)
     * LettuceConnectionFactory 생성하여 반환
     *
     */

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String redisHost = redisYml.getHost();
        Integer redisPost = redisYml.getPort();
        return new LettuceConnectionFactory(redisHost,redisPost);
    }


    /**
     * 저장된 키와 값 읽기
     * StringRedisSerializer 을 이용해서 저장된 키와 값을 읽을 수 있다.
     * redisTemplate을 받아와서 set get delete를 사용
     *  setKeySerializer, setValueSerializer 설정
     *  redis-cli을 통해 직접 데이터를 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // 아래 두 라인을 작성하지 않으면, key값이 \xac\xed\x00\x05t\x00\x03sol 이렇게 조회된다.
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
