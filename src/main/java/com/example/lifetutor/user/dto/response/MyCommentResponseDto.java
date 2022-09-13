package com.example.lifetutor.user.dto.response;

import com.example.lifetutor.comment.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyCommentResponseDto {
    private Long posting_id;
    private String nickname;
    private String title;
    private LocalDateTime date;
    private String posting_content;
    private List<Comment> comment;
    private List<String> hashtag;
    private int comment_count;
    private int like_count;
}

