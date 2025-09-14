package com.example.tomo.Promise;

import com.example.tomo.Moim.Moim;
import jakarta.persistence.*;
import lombok.Getter;

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

}
