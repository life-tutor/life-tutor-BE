package com.example.lifetutor.commentLikes.service;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.comment.repository.CommentRepository;
import com.example.lifetutor.commentLikes.model.CommentLikes;
import com.example.lifetutor.commentLikes.repository.CommentLikesRepository;
import com.example.lifetutor.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
@Service
@Transactional
public class CommentLikesService {

    private final CommentLikesRepository likesRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentLikesService(CommentLikesRepository likesRepository, CommentRepository commentRepository) {
        this.likesRepository = likesRepository;
        this.commentRepository = commentRepository;
    }

    // 공감
    public void likes(Long commentId, User user){
        Comment comment = commentNotFound(commentId);
        //이미 공감한 상태인지 확인
        if(isLikes(comment,user)) throw new IllegalArgumentException("이미 공감하셨습니다.");
        CommentLikes likes = new CommentLikes(user,comment);
        likesRepository.save(likes);
    }

    // 공감 삭제
    public void unLikes(Long commentId, User user){
        Comment comment = commentNotFound(commentId);
        if(!isLikes(comment,user)) throw new IllegalArgumentException("공감한적 없습니다.");
        CommentLikes likes = foundLikes(comment,user);
        likesRepository.deleteById(likes.getId());
    }

    //logic check
    public Comment commentNotFound(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("댓글을 찾을 수 없습니다.")
        );
    }
    public CommentLikes foundLikes(Comment comment, User user){
        return likesRepository.findByCommentAndUser(comment,user);
    }
    public boolean isLikes(Comment comment, User user){
        return foundLikes(comment,user)!=null;
    }
}
