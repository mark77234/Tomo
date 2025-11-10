package com.example.tomo.Moim_people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoimPeopleRepository extends JpaRepository<Moim_people, Long> {

    @Query("SELECT m " +
            "FROM Moim_people m " +
            "WHERE m.user.id = :user_id")
    List<Moim_people> findByUserId(@Param("user_id") Long userId);

    @Query("SELECT m.user.id " +
            "FROM Moim_people m " +
            "WHERE m.moim.id =:moim_id")
    List<Long> findUserIdsByMoimId(@Param("moim_id") Long moimId);

    @Query("SELECT COUNT(m) > 0 " +
            "FROM Moim_people m " +
            "WHERE m.moim.id = :moim_id and m.user.id = :user_id and m.leader = TRUE")
    Boolean findLeaderByMoimIdAndUserId(@Param("moim_id") Long moimId, @Param("user_id") Long userId);

    //리더 모임 ID 조회
    @Query("SELECT mp.moim.id " +
            "FROM Moim_people mp " +
            "WHERE mp.user.id = :userId AND mp.leader = TRUE")
    List<Long> findLeaderMoimIds(@Param("userId") Long userId);

    //️ 리더 모임 참여자 삭제
    @Modifying
    @Query("DELETE " +
            "FROM Moim_people mp " +
            "WHERE mp.moim.id IN :moimIds")
    void deleteMoimPeopleByMoimIds(@Param("moimIds") List<Long> moimIds);


    @Modifying
    @Query("DELETE " +
            "FROM Moim_people mp " +
            "WHERE mp.user.id = :userId AND mp.leader = FALSE")
    void deleteUserFromNonLeaderMoims(@Param("userId") Long userId);

    @Query("""
        SELECT COUNT(DISTINCT mp1.moim.id)
        FROM Moim_people mp1
        JOIN Moim_people mp2
            ON mp1.moim.id = mp2.moim.id
        WHERE mp1.user.id = :meId
          AND mp2.user.id = :otherId
    """)
    long countCommonMoims(@Param("meId") Long meId,
                          @Param("otherId") Long otherId);


}


