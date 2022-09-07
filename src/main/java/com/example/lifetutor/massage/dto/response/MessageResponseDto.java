package com.example.lifetutor.massage.dto.response;

import com.example.lifetutor.massage.dto.request.MessageRequestDto;

public class MessageResponseDto {
    private final MessageRequestDto.Enter enter;
    private final String nickname;
    private final String time;
    private final MessageRequestDto.MessageType messageType;
    private final String message;

    public MessageResponseDto(String time, MessageRequestDto requestDto) {
        this.enter = requestDto.getEnter();
        this.nickname = requestDto.getNickname();
        this.time = time;
        this.messageType = requestDto.getMessageType();
        this.message = requestDto.getMessage();
    }

    public MessageRequestDto.Enter getEnter() {
        return enter;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTime() {
        return time;
    }

    public MessageRequestDto.MessageType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }
}
