package com.example.lifetutor.room.dto.response;

import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.user.model.Role;
import lombok.Getter;

import java.util.List;

@Getter
public class ContentResponseDto {
    private final Long roomId;
    private final String username;
    private final String nickname;
    private final Role user_type;
    private final String title;
    private final boolean isfull;
    private final List<String> hashtag;

    public ContentResponseDto(Room room, List<String> hashtag) {
        this.roomId = room.getId();
        this.username = room.getUser().getUsername();
        this.nickname = room.getUser().getNickname();
        this.user_type = room.getUser().getUser_type();
        this.title = room.getTitle();
        this.isfull = room.getEnters().size() >= 2;
        this.hashtag = hashtag;
    }
}
