package com.example.tomo.Friends.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
// 이 DTO 는 그냥 응답용으로 사용하고
@NoArgsConstructor
public class ResponseFriendDetailDto {


    private String email;
    private Integer friendship;
    private LocalDate createdAt;

    public ResponseFriendDetailDto( String email, Integer friendship, LocalDate createdAt) {
        this.email = email;
        this.friendship = friendship;
        this.createdAt = createdAt;
    }

}