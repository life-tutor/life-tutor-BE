package com.example.lifetutor.hashtag.repository;

import com.example.lifetutor.hashtag.model.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    List<PostHashtag> findAllByPostId(Long id);
}
