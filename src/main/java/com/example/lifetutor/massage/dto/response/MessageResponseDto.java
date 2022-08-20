package com.example.lifetutor.massage.dto.response;

import com.example.lifetutor.massage.dto.request.MessageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponseDto {
    private MessageRequestDto.Enter enter;
    private String nickname;
    private String time;
    private MessageRequestDto.MessageType messageType;
    private String message;

    public MessageResponseDto(String time, MessageRequestDto requestDto) {
        this.enter = requestDto.getEnter();
        this.nickname = requestDto.getNickname();
        this.time = time;
        this.messageType = requestDto.getMessageType();
        this.message = requestDto.getMessage();
    }
}
