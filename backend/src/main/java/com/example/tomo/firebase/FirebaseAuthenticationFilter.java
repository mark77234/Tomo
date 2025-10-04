package com.example.tomo.firebase;

import com.example.tomo.jwt.JwtTokenProvider;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseService firebaseService;
    private final JwtTokenProvider jwtTokenProvider;

    public FirebaseAuthenticationFilter(FirebaseService firebaseService,
                                        JwtTokenProvider jwtTokenProvider) {
        this.firebaseService = firebaseService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println("Authorization header: " + header);

        String path = request.getRequestURI();

        // 🔹 Public 요청이면 JWT/FireBase 인증 필터 건너뛰기
        if (path.startsWith("/public") || path.equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/v3")) {
            filterChain.doFilter(request, response);
            return;
        }


        // Preflight 요청(CORS OPTIONS)은 그냥 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                FirebaseToken decodedToken = firebaseService.verifyIdToken(idToken);
                String uuid = decodedToken.getUid();
                System.out.println("Token verified: " + uuid);

                // 1️⃣ Spring Security 인증 객체 설정
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(uuid, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);

                // 2️⃣ JWT Access & Refresh 토큰 생성
                String accessToken = jwtTokenProvider.createAccessToken(uuid);
                String refreshToken = jwtTokenProvider.createRefreshToken(uuid);

                // 3️⃣ 응답 헤더에 추가
                response.setHeader("Authorization", "Bearer " + accessToken);
                response.setHeader("Refresh-Token", refreshToken);

            } catch (FirebaseAuthException e) {
                System.out.println("[DEBUG] Token verification failed: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired Firebase ID token");
                return; // 인증 실패 시 바로 종료
            }
        } else {
            // 헤더 없음 → 인증 실패 처리
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
