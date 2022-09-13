package com.example.lifetutor.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Builder
@Entity
public class Auth {

    @Id
    private String username;

    private String refreshToken;

    public Auth(String username, String refreshToken) {
        this.username = username;
        this.refreshToken = refreshToken;
    }
    public void updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
