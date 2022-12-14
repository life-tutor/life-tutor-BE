package com.example.lifetutor.user.dto.response;

import com.example.lifetutor.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShowMyPostsResponseDto {
    private List<ContentResponseDto> content;
    private Role user_type;
    private boolean isLast;
}
