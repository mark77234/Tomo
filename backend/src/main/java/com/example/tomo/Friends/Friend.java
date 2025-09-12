package com.example.tomo.Friends;


import com.example.tomo.Users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "friend")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="friend_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="friend_user_id")
    private User friend;

    public Friend() {}

    public Friend(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }
}
