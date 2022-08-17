package com.example.lifetutor.post.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Content {
    private Long posting_id;
    private String nickname;
    private String title;
    private LocalDate date;
    private String posting_content;
    private int comment_count;
    private int like_count;
    private boolean isLike;
    private List<String> hashtag;

    public Content(Long postingId, String nickname, String title, LocalDate date, String posting_content, boolean b, List<String> hashtag, int comment_count, int like_count) {
        this.posting_id = postingId;
        this.nickname = nickname;
        this.title = title;
        this.date = date;
        this.posting_content = posting_content;
        this.isLike = b;
        this.hashtag = hashtag;
        this.comment_count = comment_count;
        this.like_count = like_count;
    }
}
