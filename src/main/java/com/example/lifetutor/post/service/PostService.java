package com.example.lifetutor.post.service;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.repository.HashtagRepository;
import com.example.lifetutor.post.dto.request.PostRequestDto;
import com.example.lifetutor.post.dto.response.Content;
import com.example.lifetutor.post.dto.response.PostResponseDto;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;

    public PostService(PostRepository postRepository, HashtagRepository hashtagRepository) {
        this.postRepository = postRepository;
        this.hashtagRepository = hashtagRepository;
    }

    public PostResponseDto getPosts(int page, int size) {
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "date");

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> contents = posts.getContent();

        List<Content> content = new ArrayList<>();
        for(Post p : contents) {
            Long postingId = p.getId();
            String nickname = p.getUser().getNickname();
            String title = p.getTitle();
            LocalDate date = p.getDate();
            String posting_content = p.getPosting_content();
            List<Hashtag> hashtag = hashtagRepository.findAllByPostId(p.getId());
            int comment_count = p.getComments().size();
            int like_count = p.getLikes().size();

            List<String> hashtags = new ArrayList<>();
            for(Hashtag h : hashtag) hashtags.add(h.getHashtag());

            Content c = new Content(postingId, nickname, title, date, posting_content, true, hashtags, comment_count, like_count);
            content.add(c);
        }

        return new PostResponseDto(content, posts.isLast());
    }

    @Transactional
    public void registerPost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        String title = postRequestDto.getTitle();
        String posting_content = postRequestDto.getPosting_content();
        String imgUrl = postRequestDto.getImgUrl();
        List<String> hashtag = postRequestDto.getHashtag();


        User user = userDetails.getUser();
        Post post  = new Post(user, title, posting_content);

        for (String s : hashtag) {
            Hashtag ht = new Hashtag(post, s);
            hashtagRepository.save(ht);
        }

        postRepository.save(post);
    }
}
