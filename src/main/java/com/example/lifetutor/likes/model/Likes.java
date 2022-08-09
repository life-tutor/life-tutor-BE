package com.example.lifetutor.likes.model;

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
    public Likes(UserDetailImpl userDetail, Post post) {
        this.user = userDetail.getUser();
        this.post = post;
        // 무한루프 체크
        if(!post.getLikes().contains(this)) post.getLikes().add(this);
    }

    // Check logic
    public void alreadyLike(User user, Post post){
        if(this.user.equals(user) && this.post.equals(post)) throw new IllegalArgumentException("이미 공감하셨습니다.");
    }
}
