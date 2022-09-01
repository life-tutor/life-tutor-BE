package com.example.lifetutor.room.repository;

import com.example.lifetutor.room.model.RoomHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomHashtagRepository extends JpaRepository<RoomHashtag, Long> {
    @Query("select rh from RoomHashtag rh join fetch rh.hashtag h")
    List<RoomHashtag> findHashtags();
}
