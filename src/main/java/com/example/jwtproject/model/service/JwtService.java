package com.example.jwtproject.model.service;

import com.example.jwtproject.common.JwtYml;
import com.example.jwtproject.model.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 토큰의 검증, 발급을 담당할 클래스
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtYml jwtYml;

    private RedisService redisService;

    {
    }
}
