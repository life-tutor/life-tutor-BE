package com.example.lifetutor.commentLikes.model;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "COMMENTLIKES")
public class CommentLikes {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @NotNull
    private Comment comment;

    public Long getId() {
        return id;
    }
    public CommentLikes(){}
    public CommentLikes(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
        // 무한루프 체크
        if(!comment.getLikes().contains(this)) comment.getLikes().add(this);
    }
}
