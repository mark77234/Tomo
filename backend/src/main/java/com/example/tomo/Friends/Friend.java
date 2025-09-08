package com.example.tomo.Friends;


import com.example.tomo.Users.User;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
@Table(name = "friend")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private User user;

}
