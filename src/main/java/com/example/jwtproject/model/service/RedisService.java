package com.example.jwtproject.model.service;

import com.example.jwtproject.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;


/**
 * redis (StringRedisTemplate) 를 이용해 key value로 값을 가져온다
 */
@Service
@RequiredArgsConstructor
public class RedisService {
    
    private final StringRedisTemplate stringRedisTemplate;
    
    
    //key 로 value 가져오기 리프레시 토큰저장할때 prefix로 Ret
    public String getRefreshToken(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }
    
    
    //리프레시 토큰을 레디스에서 특정 유효시간 동안만 저장되도록 함
    public void setRefreshToken(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);//24초
        valueOperations.set(key, value, expireDuration);
    }
    
    //삭제하기
    public void deleteData(String key) {
        stringRedisTemplate.delete(key);
    }
    
    //로그인한 유저의 객체 데이터들을 저장 username 즉 userId를 key로 사용
    
    /**
     * 로그인한 유저의 권한을 저장 Role_username Role이라는 prefix를 앞에다가 추가_userId로 key
     */
//    public void setUserRole(String key, List value, long duration) {
//        ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();//List를 쉽게 Serialize/ Deserialize 해주는 Interface
//        Duration expireDuration = Duration.ofSeconds(12);
//        String prefix = "ROLE_";
//        System.out.println("test" + value.toString());
//        String roleKey = prefix + key;
//        String roles = value.toString();
//        System.out.println("roles" + roles);
//
//        listOperations.rightPush("testKey", "a");
//    }
    public void setUserRole(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String prefix = "ROLE_";
        String roleKey = prefix + key;
        String roles = value.toString();
        Duration expireDuration = Duration.ofMillis(12000);
        valueOperations.set(roleKey, roles, expireDuration);
    }
    
    
    /**
     * 유저의 권한 조회하기
     */
    public String getUseRole(String key) {
        String roles;
        
        String prefix = "ROLE_";
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String roleKey = prefix + key;
        roles = valueOperations.get(roleKey);
        
        if (roles != null) {
            System.out.println(roles);
            return roles;
        }
        return null;
    }
    
    
    /**
     * 유저의 정보 저장
     *
     */
    
    public void setUserDate(String username, PrincipalDetails principal, long accessTime) {
    
        System.out.println("username: "+username);
        System.out.println("principal: "+principal.getMember().getName());
    
        System.out.println("principal: "+principal.getMember().getUserPhone());
    
        System.out.println("principal: "+principal.getMember().getUserEmail().toString());
    }
}

 
 
 /**
 * 레디스에 저장할것
 * 유저의 데이터 username을 키로해서 list나 map으로
 * 유저의 권한목록 Role_username을 키로해서
 * 리프레시 토큰 Ref_username
 * <p>
 * 목록을 담는 방법
 * redisTemplate.opsForValue().set("username", "usernameValue");
 * redisTemplate.opsForValue().set("password", "passwordValue");
 */

/**
 * 레디스에 저장할것
 * 유저의 데이터 username을 키로해서 list나 map으로
 * 유저의 권한목록 Role_username을 키로해서
 * 리프레시 토큰 Ref_username
 *
 * 목록을 담는 방법
 redisTemplate.opsForValue().set("username", "usernameValue");
 redisTemplate.opsForValue().set("password", "passwordValue");
 
 */
