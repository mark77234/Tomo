package com.example.tomo.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone); // 친구 추가용
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
}
