package com.example.lifetutor.comment.dto.request;

import javax.validation.constraints.NotBlank;

public class CommentRequestDto {
    @NotBlank(message = "값을 입력해주세요.")
    private String content;

    public String getContent() {
        return content;
    }
}
