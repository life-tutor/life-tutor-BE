package com.example.lifetutor.hashtag.dto.response;

import lombok.Getter;

@Getter
public class HashtagDto {
    private String hashtag;
    private int count;

    public HashtagDto(){}
    public HashtagDto(String hashtag, int count) {
        this.hashtag = hashtag;
        this.count = count;
    }
}
