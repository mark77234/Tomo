package com.example.tomo.Friends;


import com.example.tomo.Users.User;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDate;



@Entity
@Getter
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

    private Integer m_score;
    private Integer b_score;

    private Integer friendship;

    private LocalDate created_at;


    @PrePersist
    public void prePersist() {

        created_at = LocalDate.now();
        m_score = 0;
        b_score = 0;
        friendship = 0;
    }

    public void updateFriendship(Integer friendship) {
        this.friendship = friendship;
    }

}
