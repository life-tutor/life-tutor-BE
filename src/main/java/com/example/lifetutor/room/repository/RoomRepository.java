package com.example.lifetutor.room.repository;

import com.example.lifetutor.room.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("select r from Room r join fetch r.user u where r.id = ?1")
    Optional<Room> findId(Long id);
    @Query(value = "select r from Room r join fetch r.user order by r.id desc", countQuery = "select count(r) from Room r")
    Page<Room> findAllByOrderByIdDesc(Pageable pageable);
    @Query(value = "select distinct r from Room r join fetch r.user u inner join r.hashtags h where h.hashtag.hashtag like %:hashtag% order by r.id desc", countQuery = "select count(r) from Room r")
    Page<Room> roomLikeHashtag(Pageable pageable, @Param("hashtag") String hashtag);
    @Query(value = "select r from Room r join fetch r.user u inner join r.hashtags h where h.hashtag.hashtag=:hashtag order by r.id desc", countQuery = "select count(r) from Room r")
    Page<Room> roomByHashtag(Pageable pageable, @Param("hashtag") String hashtag);
}
