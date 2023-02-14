package com.example.jwtproject.model.repository;


import com.example.jwtproject.model.domain.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JyAdminRepository {
    List<Member> selectUserList();

}
