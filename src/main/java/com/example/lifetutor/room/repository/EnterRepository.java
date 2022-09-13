package com.example.lifetutor.room.repository;

import com.example.lifetutor.room.model.Enter;
import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnterRepository extends JpaRepository<Enter,Long> {
    @Query("select e from Enter e join fetch e.room r join fetch e.user u where r=?1 and u=?2")
    Enter findByRoomAndUser(Room room,User user);
}
