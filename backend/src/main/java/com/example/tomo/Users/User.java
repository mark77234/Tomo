package com.example.tomo.Users;

import com.example.tomo.Friends.Friend;

import com.example.tomo.Moim_people.Moim_people;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name ="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="user_id")
    private Long id;

    private String firebaseId;
    private String username;
    private String email;
    private String phone;

    public User(){}

    @OneToMany(mappedBy = "user")
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Moim_people> moimPeopleList= new ArrayList<>();

    public User(String id, String username, String email) {
        this.firebaseId = id;
        this.username = username;
        this.email = email;
    }




}
