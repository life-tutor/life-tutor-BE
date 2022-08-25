package com.example.lifetutor.room.dto.response;

import java.util.List;

public class RoomResponseDto {
    private boolean isLast;
    private List<ContentResponseDto> content;

    public RoomResponseDto(boolean isLast, List<ContentResponseDto> content) {
        this.isLast = isLast;
        this.content = content;
    }

    public boolean getIsLast() {
        return isLast;
    }

    public List<ContentResponseDto> getContent() {
        return content;
    }
}
