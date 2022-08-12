package com.example.lifetutor.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Content {
    private Long posting_id;
    private String nickname;
    private String title;
    private LocalDate date;
    private String posting_content;
    private String hashtag;
    private int comment_count;
    private int like_count;
    private boolean isLike;

    public Content(Long postingId, String nickname, String title, LocalDate date, String posting_content, boolean b) {
        this.posting_id = postingId;
        this.nickname = nickname;
        this.title = title;
        this.date = date;
        this.posting_content = posting_content;
        this.isLike = b;
    }
}
