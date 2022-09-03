package com.example.lifetutor.room.model;

import com.example.lifetutor.user.model.User;

import javax.persistence.*;

@Entity
public class Enter {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="ROOM_ID", nullable = false)
    private Room room;

    public Enter(){}
    public Enter(User user, Room room){
        this.user = user;
        this.room = room;
        if(!room.getEnters().contains(this)) room.getEnters().add(this);
    }

    public User getUser() {
        return user;
    }
}
