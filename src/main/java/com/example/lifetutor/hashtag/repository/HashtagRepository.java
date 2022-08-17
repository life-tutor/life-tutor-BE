package com.example.lifetutor.hashtag.repository;

import com.example.lifetutor.hashtag.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findAllByPostId(Long id);
}
