package com.example.tomo.Friends.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseFriendDetailDto {
    private String email;
    private String username;
    private Integer friendship;
    private LocalDate createdAt;

    public ResponseFriendDetailDto(String email, String username,
                                   Integer friendship, LocalDate createdAt) {
        this.email = email;
        this.username = username;
        this.friendship = friendship;
        this.createdAt = createdAt;
    }

}
