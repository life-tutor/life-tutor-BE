package com.example.lifetutor.comment.controller;

import com.example.lifetutor.comment.dto.request.CommentRequestDto;
import com.example.lifetutor.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private CommentService commentService;

    // 댓글 작성
    @PostMapping("/api/board/{postingId}/comment")
    public ResponseEntity<String> createComment(@PathVariable Long postingId, CommentRequestDto requestDto, @AuthenticationPrincipal UserDetailImpl userDetail){
        return commentService.create(postingId, requestDto, userDetail);
    }

    // 댓긍 수정
    @PutMapping("/api/board/{postingId}/comment/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long postingId, @PathVariable Long commentId, CommentRequestDto requestDto, @AuthenticationPrincipal UserDetailImpl userDetail){
        return commentService.update(commentId, requestDto, userDetail);
    }

    // 댓글 삭제
    @DeleteMapping("/api/board/{postingId}/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long postingId, @PathVariable Long commentId, @AuthenticationPrincipal UserDetailImpl userDetail){
        return commentService.delete(commentId, userDetail);
    }
}
