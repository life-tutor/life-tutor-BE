package com.example.lifetutor.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveUserRequestDto {
    private String password;
    private String checkPassword;
}
