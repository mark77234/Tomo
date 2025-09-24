package com.example.tomo.Moim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoimRepository extends JpaRepository<Moim, Long> {
    // 모임 생성하기

    // 모임명으로 이미 만들어진 모임인지 확인하기
    Boolean existsByMoimName(String moimName);




}
