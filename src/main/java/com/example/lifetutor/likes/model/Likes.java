package com.example.lifetutor.likes.model;

import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.user.model.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Likes {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    public Long getId() {
        return id;
    }
    public Likes(){}
    public Likes(User user, Post post) {
        this.user = user;
        this.post = post;
        // 무한루프 체크
        if(!post.getLikes().contains(this)) post.getLikes().add(this);
    }
}
