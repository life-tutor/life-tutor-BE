package com.example.lifetutor.post.service;

import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.model.PostHashtag;
import com.example.lifetutor.hashtag.repository.HashtagRepository;
import com.example.lifetutor.hashtag.repository.PostHashtagRepository;
import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.post.dto.request.PostRequestDto;
import com.example.lifetutor.post.dto.response.Content;
import com.example.lifetutor.post.dto.response.PostResponseDto;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    @Autowired
    public PostService(PostRepository postRepository, HashtagRepository hashtagRepository, PostHashtagRepository postHashtagRepository) {
        this.postRepository = postRepository;
        this.hashtagRepository = hashtagRepository;
        this.postHashtagRepository = postHashtagRepository;
    }

    @Transactional
    public PostResponseDto getPosts(int page, int size, UserDetailsImpl userDetails) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> contents = posts.getContent();

        List<Content> content = new ArrayList<>();
        for (Post p : contents) {
            Long postingId = p.getId();
            String nickname = p.getUser().getNickname();
            String title = p.getTitle();
            LocalDateTime date = p.getDate();
            String posting_content = p.getPosting_content();
            List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostId(p.getId());
            int comment_count = p.getComments().size();
            int like_count = p.getLikes().size();
            boolean isLike = false;

            List<Likes> likes = p.getLikes();
            for (Likes l : likes) {
                if (l.getUser().getId() == userDetails.getUser().getId()) {
                    isLike = true;
                    break;
                }
            }

            List<String> hashtags = new ArrayList<>();

            for (PostHashtag h : postHashtags) {
                Hashtag ht = hashtagRepository.findById(h.getHashtag().getId())
                        .orElseThrow(() -> new IllegalArgumentException("No search date"));
                hashtags.add(ht.getHashtag());
            }

            Content c = new Content(postingId, nickname, title, date, posting_content, hashtags, comment_count, like_count, isLike);
            content.add(c);
        }
        return new PostResponseDto(content, posts.isLast());
    }

    @Transactional
    public void registerPost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        String title = postRequestDto.getTitle();
        String posting_content = postRequestDto.getPosting_content();
//        String imgUrl = postRequestDto.getImgUrl();
        List<String> hashtag = postRequestDto.getHashtag();


        User user = userDetails.getUser();
        Post post = new Post(user, title, posting_content);

        for (String s : hashtag) {
            Hashtag ht = hashtagRepository.findByHashtag(s);
            if (ht == null) {
                Hashtag enrollmentHt = new Hashtag(s);
                PostHashtag postHashtag = new PostHashtag(post, enrollmentHt);
                hashtagRepository.save(enrollmentHt);
                postHashtagRepository.save(postHashtag);
            } else {
                PostHashtag postHashtag = new PostHashtag(post, ht);
                postHashtagRepository.save(postHashtag);
            }

        }

        postRepository.save(post);
    }
}
