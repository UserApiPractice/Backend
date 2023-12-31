package com.example.demo.service;

import com.example.demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Value("${jwt.secret}")
    private String secretKey;

    private Long expiredMs = 1000 * 60 * 60l;

    public String login(String userName){
        return JwtUtil.createJwt(userName, secretKey, expiredMs);
    }
}
