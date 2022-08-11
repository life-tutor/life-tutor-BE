package com.example.lifetutor.post.controller;

import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/api/main/postings")
    public Page<Post> getPosts(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return postService.getPosts(page, size);
    }
}
