package com.example.tomo.Friends;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DateTimeException;
import java.util.Date;

@Getter
@Setter
// 이 DTO 는 그냥 응답용으로 사용하고
@NoArgsConstructor
public class ResponseFriendDetailDto {

    private String username;
    private Double friendship;
    private String createdAt;

    public ResponseFriendDetailDto(String username, Double friendship, String createdAt) {
        this.username = username;
        this.friendship = friendship;
        this.createdAt = createdAt;
    }

}
