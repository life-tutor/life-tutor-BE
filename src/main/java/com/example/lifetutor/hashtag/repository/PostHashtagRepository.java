package com.example.lifetutor.hashtag.repository;

import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.model.PostHashtag;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.room.model.RoomHashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    List<PostHashtag> findAllByPostId(Long id);

    void deleteByPostId(Long postingId);

    List<PostHashtag> findAllByHashtagId(Long id);

    @Query("select ph from PostHashtag ph join fetch ph.hashtag h order by h.hashtag asc")
    List<PostHashtag> findByHashtags();
}
