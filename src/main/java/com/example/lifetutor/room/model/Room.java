package com.example.lifetutor.room.model;

import com.example.lifetutor.room.dto.request.RoomRequestDto;
import com.example.lifetutor.user.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Room {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="USER_ID", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private final List<Enter> enters = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private final List<RoomHashtag> hashtags = new ArrayList<>();

    public Room(){}

    public Room(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public User getUser() {
        return user;
    }

    public List<RoomHashtag> getHashtags() {
        return hashtags;
    }

    public List<Enter> getEnters() {
        return enters;
    }

    public void update(RoomRequestDto requestDto){
        this.title = requestDto.getTitle();
    }

    // Check logic
    public void validateUser(User user){
        if(!user.getUsername().equals(this.user.getUsername())) throw new IllegalArgumentException("작성자가 아닙니다.");
    }
}
