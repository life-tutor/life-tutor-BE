package com.example.lifetutor.room.model;

import com.example.lifetutor.room.dto.request.RoomRequestDto;
import com.example.lifetutor.user.model.User;

import javax.persistence.*;
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

    @OneToOne(fetch = FetchType.LAZY,optional = false,cascade = CascadeType.ALL)
    @JoinColumn(name="ENTER_ID", nullable = false)
    private Enter enter;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomHashtag> hashtags;

    public Room(){}
    public Room(RoomRequestDto requestDto, User user, Enter enter) {
        this.title = requestDto.getTitle();
        this.user = user;
        this.enter = enter;
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

    public Enter getEnter() {
        return enter;
    }

    public void update(RoomRequestDto requestDto){
        this.title = requestDto.getTitle();
    }

    // Check logic
    public void validateUser(User user){
        if(!user.getUsername().equals(this.user.getUsername())) throw new IllegalArgumentException("작성자가 아닙니다.");
    }
}
