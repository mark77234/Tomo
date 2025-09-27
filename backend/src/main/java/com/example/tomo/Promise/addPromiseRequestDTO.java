package com.example.tomo.Promise;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class addPromiseRequestDTO {

    private String moimName; // 모임명, 존재하지 않는 모임에서 약속을 생성하는 것을 불가능하게 설정
    private String promiseName; // 약속명
    private LocalDate promiseDate; // 약속 날짜
    private LocalTime promiseTime; // 약속 시간
    private String place; // 약속 장소


}
