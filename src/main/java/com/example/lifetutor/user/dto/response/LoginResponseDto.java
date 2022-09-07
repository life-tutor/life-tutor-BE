package com.example.lifetutor.user.dto.response;

import com.example.lifetutor.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String username;
    private String nickname;
    private Role user_type;
    private boolean isKakao;
}
