package com.example.tomo.Friends.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
// 이 DTO 는 그냥 응답용으로 사용하고
@NoArgsConstructor
public class ResponseFriendDetailDto {

    private String username;
    private String email;
    private Double friendship;
    private String createdAt;

    public ResponseFriendDetailDto(String username, String email, Double friendship, String createdAt) {
        this.username = username;
        this.email = email;
        this.friendship = friendship;
        this.createdAt = createdAt;
    }

}