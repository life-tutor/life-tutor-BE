package com.example.lifetutor.likes.controller;

import com.example.lifetutor.likes.service.LikesService;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
public class LikesController {

    private final LikesService likesService;

    @Autowired
    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    // 공감
    @PostMapping("/{postingId}/likes")
    public ResponseEntity<String> likes(@PathVariable Long postingId, @AuthenticationPrincipal User user){
        likesService.likes(postingId,user);
        return new ResponseEntity<>("공감 성공",HttpStatus.CREATED);
    }

    // 공감 삭제
    @DeleteMapping("/{postingId}/likes")
    public ResponseEntity<String> unLikes(@PathVariable Long postingId, @AuthenticationPrincipal User user){
        likesService.unLikes(postingId,user);
        return new ResponseEntity<>("공감 취소",HttpStatus.OK);
    }
}
