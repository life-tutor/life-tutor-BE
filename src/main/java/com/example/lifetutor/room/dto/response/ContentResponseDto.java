package com.example.lifetutor.room.dto.response;

import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.user.model.Role;
import lombok.Getter;

import java.util.List;

@Getter
public class ContentResponseDto {
    private Long roomId;
    private String username;
    private String nickname;
    private Role user_type;
    private String title;
    private boolean isfull;
    private List<String> hashtag;

    public ContentResponseDto(Room room, List<String> hashtag) {
        this.roomId = room.getId();
        this.username = room.getUser().getUsername();
        this.nickname = room.getUser().getNickname();
        this.user_type = room.getUser().getUser_type();
        this.title = room.getTitle();
        this.isfull = room.getEnter().getAmount() >= 2;
        this.hashtag = hashtag;
    }
}
