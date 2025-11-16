package com.example.tomo.jwt;

import com.example.tomo.global.Exception.InvalidTokenException;
import com.example.tomo.global.Exception.TokenExpiredException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 시크릿 키 (512bit)
    //private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        // 주입받은 secretKey 문자열로 Key 객체를 생성
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


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

    // AccessToken 검증 후 UUID 반환
    public String validateTokenAndGetUuid(String token) throws TokenExpiredException, InvalidTokenException {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Access token expired");
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid access token");
        }
    }

    // RefreshToken 검증 후 UUID 반환
    public String validateRefreshTokenAndGetUuid(String token) throws TokenExpiredException, InvalidTokenException {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Refresh token expired");
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }
    }
}