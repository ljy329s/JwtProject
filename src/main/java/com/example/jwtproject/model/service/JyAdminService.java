package com.example.jwtproject.model.service;

import com.example.jwtproject.model.domain.Member;
import com.example.jwtproject.model.repository.JyAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class JyAdminService {
    private final JyAdminRepository jyAdminRepository;

    public List<Member> selectUserList() {
        return jyAdminRepository.selectUserList();
    }
}
