package com.example.lifetutor.post.service;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.hashtag.model.Hashtag;
import com.example.lifetutor.hashtag.model.PostHashtag;
import com.example.lifetutor.hashtag.repository.HashtagRepository;
import com.example.lifetutor.hashtag.repository.PostHashtagRepository;
import com.example.lifetutor.likes.model.Likes;
import com.example.lifetutor.post.dto.request.PostRequestDto;
import com.example.lifetutor.post.dto.response.CommentDto;
import com.example.lifetutor.post.dto.response.ContentDto;
import com.example.lifetutor.post.dto.response.PostResponseDto;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.model.Role;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

        List<ContentDto> content = new ArrayList<>();
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

            Role user_type = p.getUser().getUser_type();

            ContentDto c = new ContentDto(postingId, nickname, title, date, posting_content, hashtags, comment_count, like_count, isLike, user_type);
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

    @Transactional
    public PostResponseDto searchHashtag(String hashtag, int page, int size, UserDetailsImpl userDetails) {

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");

        Pageable pageable = PageRequest.of(page, size, sort);
        String[] tags = hashtag.split(",");

        HashSet<Long> postIds = new HashSet<>();
        for (String s : tags) {
            Hashtag tag = hashtagRepository.findByHashtag(s);
            List<PostHashtag> postHashtags = postHashtagRepository.findAllByHashtagId(tag.getId());
            for (int i = 0; i < postHashtags.size(); i++) {
                PostHashtag postHashtag = postHashtags.get(i);
                postIds.add(postHashtag.getPost().getId());
            }
        }
        int i=0;
        Long[] idArr = new Long[postIds.size()];
        for (Long id : postIds) {
            idArr[i] = id;
            i++;
        }

        Page<Post> posts = postRepository.findAll(pageable, idArr);

        List<ContentDto> content = new ArrayList<>();
        for (Post post : posts) {
            Long postingId = post.getId();
            String nickname = post.getUser().getNickname();
            String title = post.getTitle();
            LocalDateTime date = post.getDate();
            String posting_content = post.getPosting_content();
            List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostId(post.getId());
            int comment_count = post.getComments().size();
            int like_count = post.getLikes().size();
            boolean isLike = false;

            List<Likes> likes = post.getLikes();
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

            Role user_type = post.getUser().getUser_type();

            ContentDto c = new ContentDto(postingId, nickname, title, date, posting_content, hashtags, comment_count, like_count, isLike, user_type);
            content.add(c);
        }

        return new PostResponseDto(content, posts.isLast());
    }

    @Transactional
    public ContentDto getPost(Long postingId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postingId).orElseThrow(
                ()->new IllegalArgumentException("No search data."));

        ContentDto contentDto = new ContentDto();

        contentDto.setPosting_id(post.getId());
        contentDto.setNickname(userDetails.getUser().getNickname());
        contentDto.setTitle(post.getTitle());
        contentDto.setDate(post.getDate());
        contentDto.setPosting_content(post.getPosting_content());

        List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostId(post.getId());
        List<String> hashtags = new ArrayList<>();

        for (PostHashtag h : postHashtags) {
            Hashtag ht = hashtagRepository.findById(h.getHashtag().getId())
                    .orElseThrow(() -> new IllegalArgumentException("No search date"));
            hashtags.add(ht.getHashtag());
        }
        contentDto.setHashtag(hashtags);

        boolean isLike = false;
        List<Likes> likes = post.getLikes();

        int like_count = likes.size();

        for (Likes l : likes) {
            if (l.getUser().getId() == userDetails.getUser().getId()) {
                isLike = true;
                break;
            }
        }
        contentDto.setLike_count(like_count);
        contentDto.setLike(isLike);

        List<Comment> comments = post.getComments();
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = new CommentDto();
            commentDto.setId(comment.getId());
            commentDto.setNickname((comment.getUser().getNickname()));
            commentDto.setContent(comment.getContent());
            commentDto.setDate(comment.getDate());
            commentDto.setLike_count(comment.getLikes().size());
            commentDto.setLike(commentDto.isLike());
            commentDto.setUser_type(comment.getUser().getUser_type());
            commentDtos.add(commentDto);
        }

        contentDto.setComments(commentDtos);
        contentDto.setUser_type(post.getUser().getUser_type());


        return contentDto;
    }

    @Transactional
    public void editPost(PostRequestDto postRequestDto, Long postingId) {
        Post post = postRepository.findById(postingId).orElseThrow(()-> new IllegalArgumentException("No search Data."));

        post.setTitle(postRequestDto.getTitle());
        post.setPosting_content(postRequestDto.getPosting_content());

        List<String> hashtag = postRequestDto.getHashtag();
        if (!(hashtag==null)) {
            postHashtagRepository.deleteByPostId(postingId);
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
        } else {
            postHashtagRepository.deleteByPostId(postingId);
        }
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postingId) {
        postRepository.deleteById(postingId);
    }
}
