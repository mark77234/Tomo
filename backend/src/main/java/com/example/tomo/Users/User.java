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

    @Column(nullable = false)
    private String firebaseId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String inviteCode;

    public User(){}

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Moim_people> moimPeopleList = new ArrayList<>();


    public User(String id, String username, String email) {
        this.firebaseId = id;
        this.username = username;
        this.email = email;
    }

    @PrePersist
    protected void setInviteCode() {
        this.inviteCode = "TOMO-" + this.firebaseId.substring(this.firebaseId.length()-4);
    }

}
