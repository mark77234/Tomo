package com.example.tomo.Moim;

import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Promise.Promise;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
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
    private final List<Promise> promiseList = new ArrayList<>();

    // 모임 사람
    @OneToMany(mappedBy = "moim")
    private final List<Moim_people> moimPeopleList = new ArrayList<>();

    private String title;

    @Lob
    private String description;

    public Moim() {
    }

    public Moim(String title, String description) {
        this.title = title;
        this.description = description;
    }

    private LocalDateTime createdAt;

    // DB에 저장해두고 가져와야함
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
