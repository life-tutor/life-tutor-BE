package com.example.lifetutor.commentLikes.repository;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.commentLikes.model.CommentLikes;
import com.example.lifetutor.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikesRepository extends JpaRepository<CommentLikes,Long> {
    CommentLikes findByCommentAndUser(Comment comment, User user);
}
