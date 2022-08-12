package com.example.lifetutor.post.service;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.post.dto.request.PostRequestDto;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page<Post> getPosts(int page, int size) {
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "date");

        Pageable pageable = PageRequest.of(page, size, sort);

        return postRepository.findAll(pageable);
    }

    public void registerPost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        String title = postRequestDto.getTitle();
        String posting_content = postRequestDto.getPosting_content();
        String imgUrl = postRequestDto.getImgUrl();
//        String hashtag;

        User user = userDetails.getUser();
        Post post  = new Post(user, title, posting_content);

        postRepository.save(post);
    }
}
