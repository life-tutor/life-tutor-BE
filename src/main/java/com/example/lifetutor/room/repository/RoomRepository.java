package com.example.lifetutor.room.repository;

import com.example.lifetutor.room.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Page<Room> findAllByOrderByIdDesc(Pageable pageable);
//    @Query("select distinct r from Room r inner join r.hashtags h where h.hashtag.hashtag like %:hashtag% order by r.id desc")
//    Page<Room> roomByHashtag(Pageable pageable, @Param("hashtag") String hashtag);
    @Query("select distinct r from Room r inner join r.hashtags h where h.hashtag=:hashtag order by r.id desc")
    Page<Room> roomByHashtag(Pageable pageable, @Param("hashtag") String hashtag);
}
