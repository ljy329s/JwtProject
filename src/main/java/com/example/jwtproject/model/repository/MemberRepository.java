package com.example.jwtproject.model.repository;


import com.example.jwtproject.model.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberRepository {
    Member selectMember(String username);
    
    String selectUserDB(String username);
}
