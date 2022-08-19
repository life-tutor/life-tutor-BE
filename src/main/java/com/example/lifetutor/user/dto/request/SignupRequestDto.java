package com.example.lifetutor.user.dto.request;

import com.example.lifetutor.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    @Email @NotNull
    private String username;
    private String nickname;
    private String password;
    private String checkPassword;
    private Role user_type;
}
