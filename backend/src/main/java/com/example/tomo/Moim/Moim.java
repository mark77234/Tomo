package com.example.tomo.Moim;

import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Promise.Promise;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name ="moim")
public class Moim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="moim_id")
    private Long id;

    @OneToMany(mappedBy = "moim")
    private List<Promise> promiseList = new ArrayList<>();

    // 모임 사람
    @OneToMany(mappedBy = "moim")
    private List<Moim_people> moimPeopleList = new ArrayList<>();
}
