package com.example.tomo.Friends.dtos;

import lombok.Getter;

import java.time.LocalDate;


@Getter
// DB에서 JPQL을 통해서 가져오는 정보를 계산해서 정보를 저장하는 DTO
public class FriendCalculatedDto {

    private final Long userId;
    private final Integer friendship;
    private final LocalDate friendPeriod;

    public FriendCalculatedDto(Long userId, LocalDate createdAt) {
        this.userId = userId;
        this.friendship = 0;
        this.friendPeriod = createdAt;

    }
}
