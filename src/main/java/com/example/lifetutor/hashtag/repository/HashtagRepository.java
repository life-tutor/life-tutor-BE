package com.example.lifetutor.hashtag.repository;

import com.example.lifetutor.hashtag.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Hashtag findByHashtag(String s);

    @Query("select h from Hashtag h where " +
            "h not in (select h1 from RoomHashtag rh inner join Hashtag h1 on rh.hashtag.id=h1.id)" +
            "or h not in (select h2 from PostHashtag ph inner join Hashtag h2 on ph.hashtag.id=h2.id)")
    List<Hashtag> unusedHashtags();
}
