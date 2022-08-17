package com.example.lifetutor.post.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PostRequestDto {

    private String title;
    private String posting_content;
    private String imgUrl;
    private List<String> hashtag;
}
