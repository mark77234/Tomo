package com.example.tomo.Friends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f.friend.id From Friend f WHERE f.user.id = :userId")
    List<Long> getFriends(@Param("userId") Long user_id);

    // 얘는 jpql로 처리해야하지 싶은데
    // select friend_user_id from Friend f where 입력받은 ID = f.user 컬럼

}
