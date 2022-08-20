package com.example.lifetutor.massage.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
public class MessageRequestDto {
    public enum MessageType{
        TEXT, IMG
    }
    public enum Enter{
        ENTER,COMM
    }
    private Enter enter;
    private MessageType messageType;
    private String nickname;
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }
}
