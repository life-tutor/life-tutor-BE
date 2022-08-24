package com.example.lifetutor.post.repository;

import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByUser(Pageable pageable, User user);

    @Query("SELECT p FROM Post p WHERE p.id in :idArr")
    Page<Post> findAll(Pageable pageable, Long[] idArr);

//    @Query("SELECT p FROM Post p WHERE p.hashtags IN :hashtags")
//    Page<Post> findAllOrderById(Pageable pageable, List<Hashtag> hashtags);

}
