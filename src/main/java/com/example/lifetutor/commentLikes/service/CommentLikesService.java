package com.example.lifetutor.commentLikes.service;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.comment.repository.CommentRepository;
import com.example.lifetutor.commentLikes.model.CommentLikes;
import com.example.lifetutor.commentLikes.repository.CommentLikesRepository;
import com.example.lifetutor.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
        Comment comment = foundComment(commentId);
        if(foundLikes(comment,user) != null) throw new IllegalArgumentException("이미 공감하셨습니다.");
        else likesRepository.save(new CommentLikes(user,comment));
    }

    // 공감 삭제
    public void unLikes(Long commentId, User user){
        CommentLikes likes = foundLikes(foundComment(commentId),user);
        if(likes == null) throw new EntityNotFoundException("공감한적 없습니다.");
        else likesRepository.delete(likes);
    }

    //logic check
    public Comment foundComment(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("댓글을 찾을 수 없습니다.")
        );
    }
    public CommentLikes foundLikes(Comment comment, User user){
        return likesRepository.findByCommentAndUser(comment,user);
    }
    public boolean isLikes(Comment comment, User user){
        return foundLikes(comment,user)!=null;
    }
}
