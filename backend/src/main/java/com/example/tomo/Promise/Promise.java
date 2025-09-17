package com.example.tomo.Promise;

import com.example.tomo.Moim.Moim;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
public class Promise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="promise_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="moim_id")
    private Moim moim;

    private String place;

    private LocalTime promiseTime;
    private LocalDate promiseDate;
    private String promiseName;

    public Promise() {}

    public Promise(String promiseName, String place,
                   LocalTime promiseTime, LocalDate promiseDate) {
        this.place = place;
        this.promiseName = promiseName;
        this.promiseDate = promiseDate;
        this.promiseTime = promiseTime;
    }

    public void setMoimBasedPromise(Moim moim){
        this.moim = moim;
    }

}
