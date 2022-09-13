package com.example.lifetutor.comment.repository;

import com.example.lifetutor.comment.model.Comment;
import com.example.lifetutor.post.model.Post;
import com.example.lifetutor.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByUser(Pageable pageable, User user);

    List<Comment> findByUserAndPost (User user , Post post);
}
