package com.example.tomo.Moim_people;

import com.example.tomo.Moim.Moim;
import jakarta.persistence.*;
import lombok.Getter;
import com.example.tomo.Users.User;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Moim_people {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="moim_id")
    private Moim moim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private User user;

}
