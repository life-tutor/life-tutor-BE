package com.example.lifetutor.massage.dto.request;

public class MessageRequestDto {
    public enum MessageType{
        TEXT, IMG
    }
    public enum Enter{
        ENTER, COMM, EXIT
    }
    private Enter enter;
    private MessageType messageType;
    private String nickname;
    private String message;

    public Enter getEnter() {
        return enter;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
