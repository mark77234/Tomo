package com.example.tomo.Users;

import com.example.tomo.Friends.Friend;
import com.example.tomo.Moim.Moim;
import com.example.tomo.Moim_people.Moim_people;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="user_id")
    private Long id;


    @OneToMany(mappedBy = "user")
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Moim_people> moimPeople= new ArrayList<>();

}
