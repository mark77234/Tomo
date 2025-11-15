package com.example.tomo.global.Exception;

// 토큰이 만료되면 발생하는 오류
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
