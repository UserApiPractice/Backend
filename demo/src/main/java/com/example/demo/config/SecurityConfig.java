package com.example.demo.config;

import com.example.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberService memberService;

        // application.properties에서 secretKey를 가져온다
    @Value("${jwt.secret}")
    private String secretKey;

    // 보안 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                // httpBasic()와 csrf() 설정: Basic 인증과 CSRF 보호를 비활성화하고 있습니다.
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                // /user/login과 /user/save는 모든 사용자에게 접근이 허용되고, GET 메서드로 /user/** 경로는 인증된 사용자만 접근 가능합니다.
                .authorizeRequests()
                .antMatchers("/user/login","/user/save").permitAll()
                .antMatchers(HttpMethod.GET, "/user/**").authenticated()
                .antMatchers(HttpMethod.POST, "/user/**").authenticated()
                .and()
                // 세션 관리 방식을 STATELESS로 설정하여 세션 사용 안함
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // JWT 인증하여 SecurityContextHolder에 저장
                .addFilterBefore(new JwtFilter(memberService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
