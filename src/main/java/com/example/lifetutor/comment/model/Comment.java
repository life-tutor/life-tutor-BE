package com.example.lifetutor.comment.model;

import com.example.lifetutor.comment.dto.request.CommentRequestDto;
import com.example.lifetutor.commentLikes.model.CommentLikes;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.utility.Timestamped;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class Comment extends Timestamped {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(nullable = false)
    String content;

    @ManyToOne
    @NotNull
    private User user;

    @ManyToOne
    @NotNull
    private Post post;

    @OneToMany(mappedBy = "comment")
    private List<CommentLikes> likes;

    public Comment() {}
    public Comment(User user, Post post, CommentRequestDto requestDto) {
        this.user = user;
        this.post = post;
        // 무한루프 체크
        if(!post.getComments().contains(this)) post.getComments().add(this);
        this.content = requestDto.getContent();
    }

    public User getUser() {
        return user;
    }

    public List<CommentLikes> getLikes() {
        return likes;
    }

    // 수정 메소드
    public void update(CommentRequestDto requestDto){
        this.content = requestDto.getContent();
    }

    // Check logic
    public void validateUser(User user){
        if(!user.getUsername().equals(this.user.getUsername())) throw new IllegalArgumentException("작성자가 아닙니다.");
    }
}
