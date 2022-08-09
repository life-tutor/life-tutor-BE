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

    public User getUser() {
        return user;
    }

    // 연관관계 편의 메소드
    public void setUser(UserDetailImpl userDetail){
        this.user = userDetail.getUser();
    }
    public void setPost(Post post){
        this.post = post;
        // 무한루프 체크
        if(!post.getLikes().contains(this)) post.getLikes().add(this);
    }
}
