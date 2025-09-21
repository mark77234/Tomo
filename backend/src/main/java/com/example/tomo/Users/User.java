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
    @Column(name ="user_id")
    private Long id;

    private String username;
    private String email;
    private String phone;

    public User(){}

    @OneToMany(mappedBy = "user")
    private List<Friend> friends = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Moim_people> moimPeopleList= new ArrayList<>();

    public void addFriend(Friend friend) {
        friends.add(friend);
        friend.setUser(this);
    }

    public void addMoimPeople(Moim_people moimPeople) {
        moimPeopleList.add(moimPeople);
        moimPeople.setUser(this);
    }

    public User(Long id, String email, String username) {
        this.id = id;
        this.username = username;
        this.email = email;
    }




}
