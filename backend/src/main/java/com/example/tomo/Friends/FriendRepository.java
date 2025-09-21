package com.example.tomo.Friends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 사용자 ID에 해당하는 친구 목록 가져오기
    @Query("SELECT f.friend.id From Friend f WHERE f.user.id = :userId")
    List<Long> getFriends(@Param("userId") Long user_id);

    // 얘는 jpql로 처리해야하지 싶은데
    // select friend_user_id from Friend f where 입력받은 ID = f.user 컬럼

    @Query ("SELECT new com.example.tomo.Friends.FriendCalculatedDto(f.friend.id , f.m_score, f.b_score, f.created_at) " +
            "From Friend f WHERE f.user.id = :userId")
    List<FriendCalculatedDto> findFriends(@Param("userId") Long user_id);

    // 일단 현재 액세스 토큰을 통해서 사용자를 인증하는 형태가 아님. 그래서 디폴트로 사용자를 등록해두고 일단 사용하기
    // 그러면 친구 등록 요청시 먼저 email로 친구로 등록할 사용자가 있는지 먼저 검사. 그 사용자가 존재하면, friend_user_id에 이 사용자의 ID를 저장해야 함

    // 나는 이렇게 만들면 JPA가 알아서 이 쿼리를 "엔티티 객체가 존재하면 TRUE 아니면 FALSE"를 반환할것이라서 생각함
    /*@Query ("SELECT f FROM Friend f WHERE f.user.id = 1 and f.friend.id =:friend_id ")
    boolean findFriendsById(@Param("friend_id") Long friend_id);*/

    @Query ("SELECT COUNT(f) > 0 FROM Friend f WHERE f.user.id = 1 and f.friend.id = :friendId")
    boolean findFriendsByIdById(@Param("friendId")Long id);

}
