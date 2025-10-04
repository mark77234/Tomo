package com.example.tomo.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 시크릿 키 (512bit)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);


    // Access 토큰 생성
    public String createAccessToken(String uuid) {
        long accessTokenValidity = 1000 * 60 * 60;
        return Jwts.builder()
                .setSubject(uuid)           // DB 조회 UUID
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key)
                .compact();
    }

    // Refresh 토큰 생성
    public String createRefreshToken(String uuid) {
        long refreshTokenValidity = 1000 * 60 * 60 * 24 * 7; // 7일
        return Jwts.builder()
                .setSubject(uuid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key)
                .compact();
    }

    /*// 토큰에서 UUID 추출
    public String getUuidFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }*/
}
