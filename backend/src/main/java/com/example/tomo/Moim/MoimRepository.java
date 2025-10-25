package com.example.tomo.Moim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoimRepository extends JpaRepository<Moim, Long> {
    // 모임 생성하기

    // 모임명으로 이미 만들어진 모임인지 확인하기
    Boolean existsByTitle(String moimName);
    Optional<Moim> findByTitle(String moimName);





}
