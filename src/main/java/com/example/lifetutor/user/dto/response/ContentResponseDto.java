package com.example.lifetutor.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponseDto {
    private Long posting_id;
    private String nickname;
    private String title;
    private LocalDate date;
    private String content;
    private List<String> hashtag;
    private int comment_count;
    private int like_count;
}
