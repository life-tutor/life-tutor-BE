package com.example.lifetutor.user.repositroy;

import com.example.lifetutor.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);

    Boolean existsByUsername(String username);

    Boolean existsByNickname(String nickname);
}
