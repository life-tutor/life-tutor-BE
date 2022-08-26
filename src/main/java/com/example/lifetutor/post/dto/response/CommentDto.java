package com.example.lifetutor.post.dto.response;

import com.example.lifetutor.user.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class CommentDto {
    private final Long id;
    private final String nickname;
    private final String content;
    private final LocalDateTime date;
    private final int like_count;
    private final boolean isLike;
    private final Role user_type;

    @Builder
    public CommentDto(Long id, String nickname, String content, LocalDateTime date, int like_count, boolean isLike, Role user_type) {
        this.id = id;
        this.nickname = nickname;
        this.content = content;
        this.date = date;
        this.like_count = like_count;
        this.isLike = isLike;
        this.user_type = user_type;
    }
}
