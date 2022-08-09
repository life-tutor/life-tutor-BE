package com.example.lifetutor.likes.controller;

import com.example.lifetutor.likes.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    // 공감
    @PostMapping("/api/board/{postingId}/likes")
    public ResponseEntity<String> getLikes(@PathVariable Long postingId, @AuthenticationPrincipal UserDetailImpl userDetail){
        return likesService.getLikes(postingId,userDetail);
    }

    // 공감 삭제
    @DeleteMapping("/api/board/{postingId}/likes")
    public ResponseEntity<String> deleteLikes(@PathVariable Long postingId, @AuthenticationPrincipal UserDetailImpl userDetail){
        return likesService.deleteLikes(postingId,userDetail);
    }
}
