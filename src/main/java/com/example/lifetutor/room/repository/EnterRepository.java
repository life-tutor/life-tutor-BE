package com.example.lifetutor.room.repository;

import com.example.lifetutor.room.model.Enter;
import com.example.lifetutor.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterRepository extends JpaRepository<Enter,Long> {
    Enter findByUser(User user);
}
