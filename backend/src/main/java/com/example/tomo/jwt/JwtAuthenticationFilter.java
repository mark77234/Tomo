package com.example.tomo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 예: request attribute로 Firebase에서 처리된 UUID 가져오기
        String uuid = (String) request.getAttribute("uuid");

        if (uuid != null) {
            // 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(uuid);
            String refreshToken = jwtTokenProvider.createRefreshToken(uuid);

            // 응답 헤더에 추가
            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Refresh-Token", refreshToken);
        }

        filterChain.doFilter(request, response);
    }
}
