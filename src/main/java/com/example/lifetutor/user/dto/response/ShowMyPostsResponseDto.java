package com.example.lifetutor.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShowMyPostsResponseDto {
    private List<ContentResponseDto> content;
    private boolean isLast;
}
