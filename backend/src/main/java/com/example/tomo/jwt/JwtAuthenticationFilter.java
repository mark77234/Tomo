package com.example.tomo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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

        String header = request.getHeader("Authorization");
        String refreshHeader = request.getHeader("Refresh-Token");
        String path = request.getRequestURI();

        // /api/protected/** 요청은 JWT 검증하지 않고 다음 필터로
        if (path.startsWith("/api/protected/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/swagger-ui") || path.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/v3")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (header != null && header.startsWith("Bearer ")) {
                String accessToken = header.substring(7);
                String uuid = jwtTokenProvider.validateTokenAndGetUuid(accessToken);

                // SecurityContext에 인증 객체 + 권한 설정
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                uuid,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")) // 최소 권한
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
                request.setAttribute("uuid", uuid);

            } else if (refreshHeader != null) {
                String uuid = jwtTokenProvider.validateRefreshTokenAndGetUuid(refreshHeader);

                // 새로운 AccessToken 발급
                String newAccessToken = jwtTokenProvider.createAccessToken(uuid);
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                // SecurityContext에 인증 객체 + 권한 설정
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                uuid,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);
                request.setAttribute("uuid", uuid);

            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
                return;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
