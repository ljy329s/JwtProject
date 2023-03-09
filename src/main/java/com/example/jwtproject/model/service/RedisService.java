package com.example.jwtproject.model.service;

import com.example.jwtproject.auth.PrincipalDetails;
import com.example.jwtproject.common.RedisYml;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 레디스 서버에서 깨지지않게 보려면 이렇게 시작해야함
 * redis-cli --raw get kr
 *
 * map 형식 조회는 hget map이름 필드명
 *              hget test123 name
 */

/**
 * redis (StringRedisTemplate) 를 이용해 key value로 값을 가져온다
 */
@Service
@RequiredArgsConstructor
public class RedisService {
    
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisYml redisYml;
    
    /**
     * 리프레시 토큰 레디스에 저장
     */
    public void setRefreshToken(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);//24초
        valueOperations.set(redisYml.getReKey() + key, value, expireDuration);
    }
    
    /**
     * 리프레시 토큰 가져오기
     */
    public String getRefreshToken(String key) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(redisYml.getReKey() + key);
    }
    
    /**
     * 로그인한 유저의 권한을 저장 Role_이라는 prefix를 앞에다가 추가
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
//    public String getUseRole(String key) {
//        String roles;
//
//        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
//
//        roles = valueOperations.get(redisYml.getRoleKey() + key);
//        if (roles != null) {
//            String cleanRoles = roles.replaceAll("[ \\[ \\] ]", "");
//
//            int count = cleanRoles.length() - cleanRoles.replace(",", "").length();//특정 문자의 갯수
//            System.out.println("유저권한 조회 : " + cleanRoles);
//
//            String[] role = cleanRoles.split(",");
//
//            for (int i = 0; i <= count; i++) {
//                System.out.println(" 유저권한 " + role[i]);
//            }
//            System.out.println(roles);
//            return roles;
//        }
//        return null;
//    }
//
    public ArrayList getUseRole(String key) {
        String roles;
    
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
    
        ArrayList rolelist = new ArrayList<>();
        roles = valueOperations.get(redisYml.getRoleKey() + key);
        if (roles != null) {
            String cleanRoles = roles.replaceAll("[ \\[ \\] ]", "");
        
            int count = cleanRoles.length() - cleanRoles.replace(",", "").length();//특정 문자의 갯수
            System.out.println("유저권한 조회 : " + cleanRoles);
        
            String[] role = cleanRoles.split(",");
        
            for (int i = 0; i <= count; i++) {
                System.out.println(" 유저권한 " + role[i]);
                rolelist.add(role[i]);
            }
            System.out.println(roles);
            return rolelist;
        }
        return null;
    }
    
    
    /**
     * 로그인시 유저의 정보들을 저장
     */
    
    public void setUserDate(String username, PrincipalDetails principal , long accessTime) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        
        Map<String, String> map = new HashMap<>();
        String name = principal.getMember().getName();
        String userPhone = principal.getMember().getUserPhone();
        String userEmail = principal.getMember().getUserEmail();
    
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
    
    /**
     * 삭제하기
     */
    public void deleteData(String key) {
        stringRedisTemplate.delete(key);
    }
    
    
    /**
     * 로그인시 유저의 권한들을 저장 테스트
     */

    public void testSetUserRole(String username,List roleList, long accessTime) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        Map<String, String> map = new HashMap<>();
         String role = null;
        for (int i=0; i<roleList.size(); i++){
            role = (String) roleList.get(i);
            System.out.println(role);
            map.put(role,role);
        }

        hashOperations.putAll(username, map);
        
        //리스트로 넣기
    }

    /**
     * 유저의 정보 조회 테스트
     */
//    public String testGetUserRole(String username, String filed) {
//        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
//        String userData = hashOperations.get(username, filed);
//        return userData;
//    }
//    public List testGetUserRole(String username) {
//        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
//        String userData = hashOperations.get(username);
//
//       List rolelist = new ArrayList<>();
//        String role = null;
//        for (int i=0; i<roleList.size(); i++){
//            role = (String) roleList.get(i);
//            System.out.println(role);
//            map.put(role,role);
//        }
//        return userData;
//    }
//
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
//List 타입은 하나의 key에 여러 개의 value를 저장할 수 있다. 유저권한을 list로 넣어볼까?