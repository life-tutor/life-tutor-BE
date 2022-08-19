package com.example.lifetutor.post.controller;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.post.dto.request.PostRequestDto;
import com.example.lifetutor.post.dto.response.ContentDto;
import com.example.lifetutor.post.dto.response.PostResponseDto;
import com.example.lifetutor.post.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/api/search/posting")
    public PostResponseDto searchHashtag(
            @RequestParam("hashtag") String hashtag,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return postService.searchHashtag(hashtag, page, size, userDetails);
    }

    @GetMapping("/api/board/detail/{postingId}")
    public ContentDto getPost(@PathVariable Long postingId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postingId, userDetails);
    }
}
