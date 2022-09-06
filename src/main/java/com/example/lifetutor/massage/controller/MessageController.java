package com.example.lifetutor.massage.controller;

import com.example.lifetutor.massage.dto.request.MessageRequestDto;
import com.example.lifetutor.massage.dto.response.MessageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RestController
public class MessageController {
    private final SimpMessageSendingOperations sendingOperations;
    @Autowired
    public MessageController(SimpMessageSendingOperations sendingOperations) {
        this.sendingOperations = sendingOperations;
    }

    @MessageMapping("/{room_id}")
    public void message(MessageRequestDto message, @DestinationVariable Long room_id){
        String nickname = message.getNickname();
        if(MessageRequestDto.Enter.ENTER.equals(message.getEnter())) message.setMessage(nickname + "님이 들어왔습니다.");
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
        MessageResponseDto result = new MessageResponseDto(time,message);
        sendingOperations.convertAndSend("/api/sub/" + room_id, result);
    }
}
