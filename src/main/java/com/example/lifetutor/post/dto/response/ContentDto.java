package com.example.lifetutor.post.dto.response;

import com.example.lifetutor.comment.model.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContentDto {
    private Long posting_id;
    private String nickname;
    private String title;

    private LocalDateTime date;
    private String posting_content;
    private int comment_count;
    private int like_count;
    private boolean isLike;
    private List<String> hashtag;
    private List<CommentDto> comments;

    public ContentDto(Long postingId, String nickname, String title, LocalDateTime date, String posting_content, List<String> hashtag, int comment_count, int like_count, boolean isLike) {
        this.posting_id = postingId;
        this.nickname = nickname;
        this.title = title;
        this.date = date;
        this.posting_content = posting_content;
        this.hashtag = hashtag;
        this.comment_count = comment_count;
        this.like_count = like_count;
        this.isLike = isLike;
    }
}
