package com.example.lifetutor.comment.controller;

import com.example.lifetutor.comment.dto.request.CommentRequestDto;
import com.example.lifetutor.comment.service.CommentService;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/board")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 작성
    @PostMapping("/{postingId}/comment")
    public ResponseEntity<String> createComment(@PathVariable Long postingId, @RequestBody @Valid CommentRequestDto requestDto, @AuthenticationPrincipal User user){
        commentService.create(postingId, requestDto, user);
        return new ResponseEntity<>("작성 성공",HttpStatus.CREATED);
    }

    // 댓글 수정
    @PutMapping("/{postingId}/comment/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long postingId, @PathVariable Long commentId, @RequestBody @Valid CommentRequestDto requestDto, @AuthenticationPrincipal User user){
        commentService.update(postingId, commentId, requestDto, user);
        return new ResponseEntity<>("수정 성공",HttpStatus.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/{postingId}/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long postingId, @PathVariable Long commentId, @AuthenticationPrincipal User user){
        commentService.delete(postingId, commentId, user);
        return new ResponseEntity<>("삭제 성공",HttpStatus.OK);
    }
}
