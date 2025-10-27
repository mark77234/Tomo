package com.example.tomo.global;

// 토큰의 위변조시 발생
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
