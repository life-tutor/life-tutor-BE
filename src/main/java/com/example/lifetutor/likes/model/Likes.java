package com.example.lifetutor.likes.model;

import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Likes {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @NotNull
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
