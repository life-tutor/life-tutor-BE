package com.example.lifetutor.room.dto.response;

import com.example.lifetutor.room.model.Room;
import com.example.lifetutor.room.model.RoomHashtag;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class RoomResponseDto {
    private boolean isLast;
    private List<ContentResponseDto> content;

    public RoomResponseDto(){}
    public RoomResponseDto(Page<Room> rooms){
        this.isLast = rooms.isLast();
        this.content = new ArrayList<>();
        for(Room room : rooms){
            List<String> tags = new ArrayList<>();
            List<RoomHashtag> roomHashtags = room.getHashtags();
            for(RoomHashtag roomHashtag : roomHashtags) tags.add(roomHashtag.getHashtag().getHashtag());
            content.add(new ContentResponseDto(room,tags));
        }
//        if(content.isEmpty()) throw new IllegalArgumentException("채팅방이 없습니다.");
    }

    public boolean getIsLast() {
        return isLast;
    }

    public List<ContentResponseDto> getContent() {
        return content;
    }
}
