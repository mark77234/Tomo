package com.example.tomo.Moim_people;

import com.example.tomo.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoimPeopleRepository extends JpaRepository<Moim_people, Long> {

    @Query("SELECT m FROM Moim_people m WHERE m.user.id = :user_id")
    List<Moim_people> findByUserId(@Param("user_id") Long userId);

    @Query("SELECT m.user FROM Moim_people m WHERE m.moim.id = :moim_id and m.leader = true")
    User findBymoimLeader(@Param("moim_id") Long moimId);

    @Query("SELECT m.user.id FROM Moim_people m WHERE m.moim.id =:moim_id")
    List<Long> findUserIdsByMoimId(@Param("moim_id") Long moimId);
}
