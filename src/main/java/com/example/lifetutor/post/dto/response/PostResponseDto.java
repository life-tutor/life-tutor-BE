package com.example.lifetutor.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostResponseDto {

    private List<ContentDto> content;

    private boolean isLast;
}
