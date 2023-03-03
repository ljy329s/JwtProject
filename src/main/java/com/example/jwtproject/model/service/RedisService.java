package com.example.jwtproject.model.service;

import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.RedisYml;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


/**
 * redis (StringRedisTemplate) 를 이용해 key value로 값을 가져온다
 */
@Service
@RequiredArgsConstructor
public class RedisService {
    
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisYml redisYml;
    
    
    //key 로 value 가져오기 리프레시 토큰저장할때 prefix로 Ret
    public String getRefreshToken(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(redisYml.getReKey() + key);
    }
    
    
    //리프레시 토큰을 레디스에서 특정 유효시간 동안만 저장되도록 함
    public void setRefreshToken(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);//24초
        valueOperations.set(redisYml.getReKey() + key, value, expireDuration);
    }
    
    //삭제하기
    public void deleteData(String key) {
        stringRedisTemplate.delete(key);
    }
    
    //로그인한 유저의 객체 데이터들을 저장 username 즉 userId를 key로 사용
    
    /**
     * 로그인한 유저의 권한을 저장 Role_username Role이라는 prefix를 앞에다가 추가_userId로 key
     */
    
    public void setUserRole(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String roles = value.toString();
        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(redisYml.getRoleKey() + key, roles, expireDuration);
    }
    
    
    /**
     * 유저의 권한 조회하기
     */
    public String getUseRole(String key) {
        String roles;
        
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        
        roles = valueOperations.get(redisYml.getRoleKey() + key);
        if (roles != null) {
            String cleanRoles = roles.replaceAll("[ \\[ \\] ]", "");
            
            int count = cleanRoles.length() - cleanRoles.replace(",", "").length();//특정 문자의 갯수
            System.out.println("유저권한 조회 : " + cleanRoles);
            
            String[] d = cleanRoles.split(",");
            
            for (int i = 0; i <= count; i++) {
                System.out.println(" 유저권한 " + d[i]);
            }
            System.out.println(roles);
            return roles;
        }
        return null;
    }
    
    
    /**
     * 유저의 정보 저장
     */
    
    public void setUserDate(String username, PrincipalDetails principal, long accessTime) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String name = principal.getMember().getName();
        String userPhone = principal.getMember().getUserPhone();
        String userEmail = principal.getMember().getUserEmail();
        
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("userPhone", userPhone);
        map.put("userEmail", userEmail);
        
        hashOperations.putAll(username, map);
        
    }
    
    /**
     * 유저의 정보 조회
     */
    public String getUserDate(String username, String filed) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        String userData = hashOperations.get(username, filed);
        return userData;
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
 * <p>
 * 레디스에 저장할것
 * 유저의 데이터 username을 key로 map으로 저장
 * 유저의 권한목록 Role_username을 ket로해서
 * 리프레시 토큰 Ref_username
 * <p>
 * 목록을 담는 방법
 * redisTemplate.opsForValue().set("username", "usernameValue");
 * redisTemplate.opsForValue().set("password", "passwordValue");
 */

/**
 * 레디스에 저장할것
 * 유저의 데이터 username을 key로 map으로 저장
 * 유저의 권한목록 Role_username을 ket로해서
 * 리프레시 토큰 Ref_username
 *
 * 목록을 담는 방법
 redisTemplate.opsForValue().set("username", "usernameValue");
 redisTemplate.opsForValue().set("password", "passwordValue");
 */
