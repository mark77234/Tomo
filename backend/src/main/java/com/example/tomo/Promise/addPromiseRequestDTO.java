package com.example.tomo.Promise;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class addPromiseRequestDTO {

    // 약속 엔티티에 약속명 추가히기
    private Long moimId;
    private String promiseName;
    private LocalDate promiseDate;
    private LocalTime promiseTime;
    private String place;


}
