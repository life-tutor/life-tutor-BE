package com.example.lifetutor.post.dto.response;

import com.example.lifetutor.user.model.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ContentDto {
    private final Long posting_id;
    private final String nickname;
    private final String title;

    private final LocalDateTime date;
    private final String posting_content;
    private final int comment_count;
    private final int like_count;
    private final boolean isLike;
    private final Role user_type;
    private final List<String> hashtag;
    private final List<CommentDto> comments;

    @Builder
    public ContentDto(Long posting_id, String nickname, String title, LocalDateTime date, String posting_content, int comment_count, int like_count, boolean isLike, Role user_type, List<String> hashtag, List<CommentDto> comments) {
        this.posting_id = posting_id;
        this.nickname = nickname;
        this.title = title;
        this.date = date;
        this.posting_content = posting_content;
        this.comment_count = comment_count;
        this.like_count = like_count;
        this.isLike = isLike;
        this.user_type = user_type;
        this.hashtag = hashtag;
        this.comments = comments;
    }
}
