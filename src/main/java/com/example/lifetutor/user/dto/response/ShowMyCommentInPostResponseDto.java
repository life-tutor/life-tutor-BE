package com.example.lifetutor.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowMyCommentInPostResponseDto {
    private List<MyCommentResponseDto> content;
    private boolean isLast;
}
