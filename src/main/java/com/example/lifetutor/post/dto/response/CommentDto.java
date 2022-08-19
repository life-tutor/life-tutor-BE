package com.example.lifetutor.post.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime date;
    private int like_count;
    private boolean isLike;
}
