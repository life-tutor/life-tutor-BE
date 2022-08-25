package com.example.lifetutor.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMyPasswordRequestDto {
    private String password;
    private String changePassword;
    private String confirmChangePassword;
}
