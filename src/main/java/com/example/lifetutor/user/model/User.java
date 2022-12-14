package com.example.lifetutor.user.model;

import com.example.lifetutor.user.dto.request.UpdateMyInfoRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "users")
@NoArgsConstructor
@Getter
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role user_type;

    @Column(nullable = false)
    private boolean isKakao;


    public User(String username, String nickname, String password, Role userType,boolean isKakao) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.user_type = userType;
        this.isKakao = isKakao;
    }

    public User(String nickname, String username, String password) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
    }


    public void updateMyInfo(UpdateMyInfoRequestDto updateMyInfoRequestDto) {
        this.nickname = updateMyInfoRequestDto.getNickname();
        this.user_type = updateMyInfoRequestDto.getUser_type();
    }

    public void updateMyPassword(String password) {
        this.password = password;
    }
}
