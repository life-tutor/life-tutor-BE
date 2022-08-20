package com.example.lifetutor.room.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class RoomRequestDto {
    private String title;
    private List<String> hashtag;
}
