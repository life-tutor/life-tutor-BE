package com.example.lifetutor.commentLikes.model;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "COMMENTLIKES")
public class CommentLikes {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="COMMENT_ID",nullable = false)
    private Comment comment;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public CommentLikes(){}
    public CommentLikes(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
        // 무한루프 체크
        if(!comment.getLikes().contains(this)) comment.getLikes().add(this);
    }
}
