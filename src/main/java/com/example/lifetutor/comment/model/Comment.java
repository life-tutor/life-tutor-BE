package com.example.lifetutor.comment.model;

import com.example.lifetutor.comment.dto.request.CommentRequestDto;

import javax.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    String content;

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    public Comment() {}
    public Comment(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public Post getPost() {
        return post;
    }

    // 연관관계 편의 메소드
    public void setUser(UserDetailImpl userDetail){
        this.user = userDetail.getUser();
    }
    public void setPost(Post post){
        this.post = post;
        // 무한루프 체크
        if(!post.getComments().contains(this)) post.getComments().add(this);
    }

    // 수정 메소드
    public void update(CommentRequestDto requestDto){
        this.content = requestDto.getContent();
    }

    // Check logic
    public void emptyValue(UserDetailImpl userDetail, CommentRequestDto requestDto){
        if(userDetail.getUser() == null) throw new IllegalArgumentException("로그인이 필요합니다.");
        if(requestDto.getContent().isEmpty()) throw new IllegalArgumentException("값을 입력해주세요.");
    }
    public void validateUser(UserDetailImpl userDetail, Comment comment){
        if(userDetail.getUser().equals(comment.getUser())) throw new IllegalArgumentException("작성자가 아닙니다.");
    }
}
