package com.example.tomo.global;

import com.example.tomo.firebase.FirebaseAuthenticationFilter;
import com.example.tomo.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // 🔹 Firebase 전용 체인
    @Bean
    @Order(1)
    public SecurityFilterChain firebaseChain(HttpSecurity http,
                                             FirebaseAuthenticationFilter firebaseAuthFilter) throws Exception {
        http
                .securityMatcher("/api/protected/**") // 이 경로만 Firebase 필터 적용
                .csrf(CsrfConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // securityMatcher에 들어간 uri 제외 모든 요청은 통과
                )
                .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔹 JWT 전용 체인
    @Bean
    @Order(2)
    public SecurityFilterChain jwtChain(HttpSecurity http,
                                        JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/protected/**").permitAll() // 이 경로는 허용
                        .anyRequest().authenticated() // 다른 모든 요청은 인증이 요구
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
