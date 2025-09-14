package com.example.tomo.Friends;

import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;


@Getter
// DB에서 JPQL을 통해서 가져오는 정보를 계산해서 정보를 저장하는 DTO
public class FriendCalculatedDto {

    private Long userId;
    private Double friendship;
    private String friendPeriod;

    public FriendCalculatedDto(Long userId, Integer m_score, Integer b_score, LocalDate createdAt) {
        this.userId = userId;
        this.friendship = 0.3 * m_score -0.2 * b_score;

        LocalDate startDate = createdAt != null ? createdAt : LocalDate.now(); // null이면 오늘로 대체
        Period period = Period.between(startDate, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();
        this.friendPeriod = years + "년 " + months + "개월";
    }
}
