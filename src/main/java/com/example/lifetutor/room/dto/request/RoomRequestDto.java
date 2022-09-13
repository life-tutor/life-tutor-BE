package com.example.lifetutor.room.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RoomRequestDto {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    private List<String> hashtag;
}
