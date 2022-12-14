package com.example.lifetutor.comment.model;

import com.example.lifetutor.comment.dto.request.CommentRequestDto;
import com.example.lifetutor.commentLikes.model.CommentLikes;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.user.model.User;
import com.example.lifetutor.utility.Timestamped;

import javax.persistence.*;
import java.util.List;

@Entity
public class Comment extends Timestamped {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(nullable = false)
    String content;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="POST_ID",nullable = false)
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentLikes> likes;

    public Comment() {}
    public Comment(User user, Post post, CommentRequestDto requestDto) {
        this.user = user;
        this.post = post;
        // 무한루프 체크
        if(!post.getComments().contains(this)) post.getComments().add(this);
        this.content = requestDto.getContent();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return  post;
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
