package com.example.demo.config;

import com.example.demo.service.MemberService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final MemberService memberService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더에서 authorization를 읽어온다
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization : {}", authorization);

        // authorization이 없거나 앞에 Bearer가 없으면 잘못된 토큰이므로 다음 필터로 넘어간다
        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.error("authorization을 잘못 보냈습니다.");
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = authorization.split(" ")[1];

        // isExpired를 통해 JWT 토큰의 만료여부를 확인한다.
        if(JwtUtil.isExpired(token, secretKey)){
            log.info("token : {}", token);
            log.info("secretKey : {}", secretKey);

            log.error("Token이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        };

        // 토큰을 사용하여 사용자 권한 정보를 생성한다.
        String username = JwtUtil.getUserName(token, secretKey);
        log.info("username : {}", username);

        // 사용자 인증 토큰을 생성한다.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("USER")));

        // 사용자 세부 정보 세팅
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // SecurityContextHolder에 생성된 authenticationToken을 넣어주자
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
