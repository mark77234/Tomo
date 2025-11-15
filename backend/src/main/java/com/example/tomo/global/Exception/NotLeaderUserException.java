package com.example.tomo.global.Exception;

public class NotLeaderUserException extends RuntimeException {
    public NotLeaderUserException(String message) {
        super(message);
    }
}
