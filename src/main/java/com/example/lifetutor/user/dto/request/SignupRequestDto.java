package com.example.lifetutor.user.dto.request;

import com.example.lifetutor.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    @NotEmpty(message = "이메일을 입력해주시기 바랍니다.")
    @Email
    private String username;
    @NotBlank(message = "닉네임을 입력해주시기 바랍니다.")
    private String nickname;
    @NotBlank
    private String password;
    @NotBlank
    private String checkPassword;
    @NotNull
    private Role user_type;
}
