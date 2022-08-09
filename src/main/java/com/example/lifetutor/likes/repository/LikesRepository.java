package com.example.lifetutor.likes.repository;

import com.example.lifetutor.likes.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes,Long> {
    Likes findByPostAndUser(Post post, User user);
}
