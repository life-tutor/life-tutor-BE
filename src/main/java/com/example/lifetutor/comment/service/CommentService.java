package com.example.lifetutor.comment.service;

import com.example.lifetutor.comment.dto.request.CommentRequestDto;
import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    public ResponseEntity<String> create(Long postingId, CommentRequestDto requestDto, UserDetailImpl userDetail){
        Post post = postRepository.findById(postingId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
        );
        Comment comment = new Comment(requestDto);
        // 입력값 확인
        comment.emptyValue(userDetail,requestDto);
        // 연관관계 편의 메소드
        comment.setPost(post);
        comment.setUser(userDetail);

        commentRepository.save(comment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 댓글 수정
    public ResponseEntity<String> update(Long commentId, CommentRequestDto requestDto, UserDetailImpl userDetail){
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("댓글을 찾을 수 없습니다.")
        );
        // 작성자 확인
        comment.validateUser(userDetail,comment);
        // 입력값 확인
        comment.emptyValue(userDetail,requestDto);
        comment.update(requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 댓글 삭제
    public ResponseEntity<String> delete(Long commentId, UserDetailImpl userDetail){
        // 작성자 확인
        commentRepository.findById(commentId).ifPresent(comment -> comment.validateUser(userDetail, comment));
        commentRepository.deleteById(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
