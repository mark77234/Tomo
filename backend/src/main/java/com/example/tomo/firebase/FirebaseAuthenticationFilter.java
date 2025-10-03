package com.example.tomo.firebase;

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

    public FirebaseAuthenticationFilter(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println("Authorization header: " + header);

        // Preflight 요청(CORS OPTIONS)은 그냥 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (header != null && header.startsWith("Bearer ")) {
            String idToken = header.substring(7);
            try {
                FirebaseToken decodedToken = firebaseService.verifyIdToken(idToken);
                System.out.println("Token verified: " + decodedToken.getUid());

                // 인증 객체 생성 (권한 목록 빈 리스트)
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(decodedToken.getUid(), null, List.of());
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (FirebaseAuthException e) {
                System.out.println("[DEBUG] Token verification failed: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired Firebase ID token");
                return; // 인증 실패 시 바로 종료
            }
        } else {
            // 헤더 없음 → 인증 실패 처리
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header!!!");
            return;
        }

        filterChain.doFilter(request, response);
    }
}