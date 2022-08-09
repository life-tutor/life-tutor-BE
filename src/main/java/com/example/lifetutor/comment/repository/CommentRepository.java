package com.example.lifetutor.comment.repository;

import com.example.lifetutor.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {}
