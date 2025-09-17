package com.example.tomo.Promise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGetPromiseDto {

    private String promiseName;
    private LocalDate promiseDate;
    private LocalTime promiseTime;
    private String place;

}
