package com.example.lifetutor.hashtag.dto.response;

import lombok.Getter;

@Getter
public class HashtagDto {
    private final String hashtag;
    private final int count;

    public HashtagDto(String hashtag, int count) {
        this.hashtag = hashtag;
        this.count = count;
    }
}
