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
    private String password;
    private String checkPassword;
    private Role user_type;
}
