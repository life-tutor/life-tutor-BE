package com.example.lifetutor.comment.service;

import com.example.lifetutor.comment.dto.request.CommentRequestDto;
import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.comment.repository.CommentRepository;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.post.repository.PostRepository;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    // 댓글 작성
    public void create(Long postingId, CommentRequestDto requestDto, User user){
        Post post = postNotFound(postingId);
        Comment comment = new Comment(user,post,requestDto);
        commentRepository.save(comment);
    }

    // 댓글 수정
    public void update(Long postingId, Long commentId, CommentRequestDto requestDto, User user){
        postNotFound(postingId);
        Comment comment = commentNotFound(commentId);
        // 작성자 확인
        comment.validateUser(user);
        comment.update(requestDto);
    }

    // 댓글 삭제
    public void delete(Long postingId, Long commentId, User user){
        postNotFound(postingId);
        commentRepository.findById(commentId).ifPresent(comment -> comment.validateUser(user));
        commentRepository.deleteById(commentId);
    }

    //logic check
    public Post postNotFound(Long postingId){
        return postRepository.findById(postingId).orElseThrow(
                () -> new EntityNotFoundException("게시글을 찾을 수 없습니다.")
        );
    }
    public Comment commentNotFound(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );
    }
}
