package com.example.lifetutor.commentLikes.controller;

import com.example.lifetutor.commentLikes.service.CommentLikesService;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentLikesController {

    private final CommentLikesService likesService;

    @Autowired
    public CommentLikesController(CommentLikesService likesService) {
        this.likesService = likesService;
    }

    // 공감
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<String> likes(@PathVariable Long commentId, @AuthenticationPrincipal User user){
        likesService.likes(commentId,user);
        return new ResponseEntity<>("공감 성공",HttpStatus.CREATED);
    }

    // 공감 삭제
    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<String> unLikes(@PathVariable Long commentId, @AuthenticationPrincipal User user){
        likesService.unLikes(commentId,user);
        return new ResponseEntity<>("공감 취소",HttpStatus.OK);
    }
}
