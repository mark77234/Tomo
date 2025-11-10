package com.example.tomo.global;

public class SelfFriendRequestException extends RuntimeException {
    public SelfFriendRequestException(String message) {
        super(message);
    }
}
