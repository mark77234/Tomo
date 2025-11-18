package com.example.tomo.Friends;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class FriendShipPolicy {

    // 기간 점수 계산
    public int calculateTimeScore(LocalDate createdAt) {
        long daysBetween = ChronoUnit.DAYS.between(createdAt, LocalDate.now());
        double weeks = daysBetween / 7.0;
        return (int) Math.floor(weeks * 5); // 일주일당 5점, 반내림
    }

    // 모임 참여 점수 계산 (임시 규칙)
    public int calculateGroupScore(int joinedCount) {
        return joinedCount * 5; // 예: 모임 참여 1회당 5점
    }

    // 최종 합산
    public int calculateTotalScore(LocalDate createdAt, int joinedCount) {
        return calculateTimeScore(createdAt) + calculateGroupScore(joinedCount);
    }
}
