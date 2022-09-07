package com.example.lifetutor.room.dto.response;

import java.util.List;

public class RoomResponseDto {
    private final boolean isLast;
    private final List<ContentResponseDto> content;

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
