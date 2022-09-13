package com.example.lifetutor.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MyCommentResponseDto {
    private Long posting_id;
    private String title;
    private String comment_content;
    private int comment_count;
    private LocalDateTime localDateTime;

    @Builder
    public MyCommentResponseDto(Long posting_id, String title, String comment_content, int comment_count, LocalDateTime localDateTime) {
        this.posting_id = posting_id;
        this.title = title;
        this.comment_content = comment_content;
        this.comment_count = comment_count;
        this.localDateTime = localDateTime;
    }
}

