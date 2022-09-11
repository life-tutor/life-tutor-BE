package com.example.lifetutor.post.repository;

import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByUser(Pageable pageable, User user);

    @Query("SELECT p FROM Post p WHERE p.id in :idArr")
    Page<Post> findAll(Pageable pageable, Long[] idArr);

    @Query("select distinct p from Post p inner join p.hashtags h where h.hashtag.hashtag=:hashtag order by p.id desc ")
    Page<Post> postByHashtag(Pageable pageable,  @Param("hashtag") String hashtag);

//    @Query("SELECT p FROM Post p WHERE p.hashtags IN :hashtags")
//    Page<Post> findAllOrderById(Pageable pageable, List<Hashtag> hashtags);

}
