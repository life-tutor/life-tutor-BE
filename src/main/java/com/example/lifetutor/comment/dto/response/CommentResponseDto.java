package com.example.lifetutor.comment.dto.response;

import com.example.lifetutor.comment.model.Comment;

import java.time.LocalDate;

public class CommentResponseDto {
    private Long id;
    private String nickname;
    private String content;
    private LocalDate date;
    private int like_count;
    private boolean isLike;

    public CommentResponseDto(Comment comment, int like_count, boolean isLike) {
        this.id = comment.getId();
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.date = comment.getDate();
        this.like_count = like_count;
        this.isLike = isLike;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getLike_count() {
        return like_count;
    }

    public boolean getIsLike() {
        return isLike;
    }
}
