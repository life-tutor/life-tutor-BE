package com.example.lifetutor.room.repository;

import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.room.model.RoomHashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomHashtagRepository extends JpaRepository<RoomHashtag, Long> {
    @Query(value = "select rh from RoomHashtag rh join fetch rh.hashtag h order by h.hashtag asc", countQuery = "select count(rh) from RoomHashtag rh")
    Page<RoomHashtag> findByHashtags(Pageable pageable);

    @Query("select rh from RoomHashtag rh join fetch rh.hashtag h where rh.room = :room order by rh.id asc")
    List<RoomHashtag> findByRoom(@Param("room")Room room);
}
