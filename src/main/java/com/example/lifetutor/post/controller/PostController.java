package com.example.lifetutor.post.controller;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.post.dto.request.PostRequestDto;
import com.example.lifetutor.post.dto.response.PostResponseDto;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/api/main/postings")
    public PostResponseDto getPosts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return postService.getPosts(page, size, userDetails);
    }

    @PostMapping("/api/board")
    public void registerPost(@RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.registerPost(postRequestDto, userDetails);
    }
}
