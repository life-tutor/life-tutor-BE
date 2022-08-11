package com.example.lifetutor.user.model;

import com.example.lifetutor.user.dto.request.UpdateMyInfoRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
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


    public User(String username, String nickname, String password, Role userType) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.user_type = userType;
    }


    public void updateMyInfo(UpdateMyInfoRequestDto updateMyInfoRequestDto) {
        this.nickname = updateMyInfoRequestDto.getNickname();
        this.password = updateMyInfoRequestDto.getPassword();
        this.user_type = updateMyInfoRequestDto.getUser_type();
    }
}
