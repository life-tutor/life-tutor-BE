package com.example.lifetutor.user.dto.request;

import com.example.lifetutor.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMyInfoRequestDto {
    private String nickname;
    private Role user_type;
}
