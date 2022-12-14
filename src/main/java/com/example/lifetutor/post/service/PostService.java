package com.example.lifetutor.post.service;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.commentLikes.service.CommentLikesService;
import com.example.lifetutor.config.security.UserDetailsImpl;
import com.example.lifetutor.hashtag.dto.response.HashtagDto;
import com.example.lifetutor.hashtag.model.CountHashtag;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PostService {

    private final CommentLikesService commentLikesService;
    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final CountHashtag countHashtag;

    @Autowired
    public PostService(CommentLikesService commentLikesService, PostRepository postRepository, HashtagRepository hashtagRepository, PostHashtagRepository postHashtagRepository, CountHashtag countHashtag) {
        this.commentLikesService = commentLikesService;
        this.postRepository = postRepository;
        this.hashtagRepository = hashtagRepository;
        this.postHashtagRepository = postHashtagRepository;
        this.countHashtag = countHashtag;
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPosts(int page, int size, UserDetailsImpl userDetails) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postRepository.findAll(pageable);
        List<Post> contents = posts.getContent();

        List<ContentDto> content = getContents(userDetails, contents);

        return new PostResponseDto(content, posts.isLast());
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostsOfNotUser(int page, int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postRepository.findAll(pageable);
        List<Post> contents = posts.getContent();

        List<ContentDto> content = new ArrayList<>();
        for (Post post : contents)
            content.add(getCustomMadePost(post));

        return new PostResponseDto(content, posts.isLast());
    }

    @Transactional
    public void registerPost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        postRequestDto.setUser(userDetails.getUser());
        Post post = new Post(postRequestDto);

        // post ??????
        postRepository.save(post);
        // hashtag ??????
        if (!postRequestDto.getHashtag().isEmpty() || postRequestDto.getHashtag() != null)
            saveHashtag(post, postRequestDto.getHashtag());
    }

    @Transactional(readOnly = true)
    public PostResponseDto searchPostings(String hashtag, int page, int size, UserDetailsImpl userDetails) {
        validateHashtag(hashtag);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.postByHashtag(pageable, hashtag);
        List<Post> contents = posts.getContent();
        List<ContentDto> content = getContents(userDetails, contents);

        return new PostResponseDto(content, posts.isLast());
    }

    @Transactional(readOnly = true)
    public List<HashtagDto> searchHashtags(String keyword) {
        validateHashtag(keyword);

        List<HashtagDto> result = new ArrayList<>();
        List<PostHashtag> hashtags = postHashtagRepository.findByHashtags();
        if(!keyword.isEmpty() && !hashtags.isEmpty())
            result = countHashtag.postHashtag(keyword, hashtags);
        return result;
    }

    private void validateHashtag(String hashtag) {
        hashtag = hashtag.trim();
        if(hashtag.isEmpty())
            throw new IllegalArgumentException("???????????? ??????????????????.");
        else
            if(hashtag.length() < 2 || hashtag.length() > 6)
                throw new IllegalArgumentException("2??? ~ 6????????? ??????????????????.");
    }

    @Transactional(readOnly = true)
    public ContentDto getPost(Long postingId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postingId).orElseThrow(
                () -> new IllegalArgumentException("No search data."));

        return getCustomMadePost(userDetails, post);
    }

    @Transactional
    public void editPost(PostRequestDto postRequestDto, Long postingId) {
        Post post = postRepository.findById(postingId)
                .orElseThrow(() -> new IllegalArgumentException("No search Data."));

        post.setTitle(postRequestDto.getTitle());
        post.setPosting_content(postRequestDto.getPosting_content());

        List<String> hashtag = postRequestDto.getHashtag();
        postHashtagRepository.deleteByPostId(postingId);
        if (!(hashtag == null)) saveHashtag(post, hashtag);

        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postingId) {
        postHashtagRepository.deleteByPostId(postingId);
        postRepository.deleteById(postingId);
    }

    private Long[] getDistinctIds(String hashtag) {
        HashSet<Long> postIds = new HashSet<>();
        Hashtag tag = hashtagRepository.findByHashtag(hashtag);

        if (tag == null) return new Long[0];

        List<PostHashtag> postHashtags = postHashtagRepository.findAllByHashtagId(tag.getId());

        for (PostHashtag postHashtag : postHashtags) {
            postIds.add(postHashtag.getPost().getId());
        }

        int i = 0;
        Long[] idArr = new Long[postIds.size()];
        for (Long id : postIds) {
            idArr[i] = id;
            i++;
        }
        return idArr;
    }

    private List<ContentDto> getContents(UserDetailsImpl userDetails, List<Post> contents) {
        List<ContentDto> content = new ArrayList<>();
        for (Post post : contents)
            content.add(getCustomMadePost(userDetails, post));
        return content;
    }

    private ContentDto getCustomMadePost(Post post) {

        // ???????????? ???????????? ????????????
        List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostId(post.getId());
        List<String> hashtags = new ArrayList<>();

        for (PostHashtag h : postHashtags) {
            Hashtag ht = hashtagRepository.findById(h.getHashtag().getId())
                    .orElseThrow(() -> new IllegalArgumentException("No search date"));
            hashtags.add(ht.getHashtag());
        }

        List<Likes> likes = post.getLikes();
        int like_count = likes.size();

        // ???????????? ?????? ????????????
        List<Comment> comments = post.getComments();
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = CommentDto.builder()
                    .id(comment.getId())
                    .nickname(comment.getUser().getNickname())
                    .content(comment.getContent())
                    .date(comment.getDate())
                    .like_count(comment.getLikes().size())
                    .isLike(false)
                    .user_type(comment.getUser().getUser_type())
                    .build();
            commentDtos.add(commentDto);
        }

        return ContentDto.builder()
                .posting_id(post.getId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .date(post.getDate())
                .posting_content(post.getPosting_content())
                .hashtag(hashtags)
                .like_count(like_count)
                .isLike(false)
                .comments(commentDtos)
                .comment_count(post.getComments().size())
                .user_type(post.getUser().getUser_type())
                .build();
    }

    private ContentDto getCustomMadePost(UserDetailsImpl userDetails, Post post) {

        // ???????????? ???????????? ????????????
        List<PostHashtag> postHashtags = postHashtagRepository.findAllByPostId(post.getId());
        List<String> hashtags = new ArrayList<>();

        for (PostHashtag h : postHashtags) {
            Hashtag ht = hashtagRepository.findById(h.getHashtag().getId())
                    .orElseThrow(() -> new IllegalArgumentException("No search date"));
            hashtags.add(ht.getHashtag());
        }

        boolean isLike = false;
        List<Likes> likes = post.getLikes();
        int like_count = likes.size();

        for (Likes l : likes) {
            if (l.getUser().getId() == userDetails.getUser().getId()) {
                isLike = true;
                break;
            }
        }

        // ???????????? ?????? ????????????
        List<Comment> comments = post.getComments();
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = CommentDto.builder()
                    .id(comment.getId())
                    .nickname(comment.getUser().getNickname())
                    .content(comment.getContent())
                    .date(comment.getDate())
                    .like_count(comment.getLikes().size())
                    .isLike(commentLikesService.isLikes(comment, userDetails.getUser()))
                    .user_type(comment.getUser().getUser_type())
                    .build();
            commentDtos.add(commentDto);
        }

        return ContentDto.builder()
                .posting_id(post.getId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .date(post.getDate())
                .posting_content(post.getPosting_content())
                .hashtag(hashtags)
                .like_count(like_count)
                .isLike(isLike)
                .comments(commentDtos)
                .comment_count(post.getComments().size())
                .user_type(post.getUser().getUser_type())
                .build();
    }

    private void saveHashtag(Post post, List<String> hashtag) {

        Set<String> tags = new HashSet<>();
        for (String s : hashtag) {
            s = s.trim();
            if (!s.equals("")) tags.add(s);
        }

        for (String s : tags) {
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
    }



}
