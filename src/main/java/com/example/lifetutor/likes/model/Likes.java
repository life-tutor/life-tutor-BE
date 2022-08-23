package com.example.lifetutor.likes.model;

import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.user.model.User;

import javax.persistence.*;

@Entity
public class Likes {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="POST_ID",nullable = false)
    private Post post;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Likes(){}
    public Likes(User user, Post post) {
        this.user = user;
        this.post = post;
        // 무한루프 체크
        if(!post.getLikes().contains(this)) post.getLikes().add(this);
    }
}
