package com.example.lifetutor.room.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class RoomRequestDto {
    private String title;
    private List<String> hashtag;

    public void isEmpty(){
        this.title = title.trim();
        if(title.isEmpty()) throw new IllegalArgumentException("제목을 입력해주세요.");
    }
}
