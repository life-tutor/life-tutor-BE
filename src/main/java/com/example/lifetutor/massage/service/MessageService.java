package com.example.lifetutor.massage.service;

import com.example.lifetutor.massage.dto.request.MessageRequestDto;
import com.example.lifetutor.massage.dto.response.MessageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class MessageService {
    private final SimpMessageSendingOperations sendingOperations;

    @Autowired
    public MessageService(SimpMessageSendingOperations sendingOperations) {
        this.sendingOperations = sendingOperations;
    }

    // 채팅 메시지
    public void message(MessageRequestDto message, Long room_id){
        String nickname = message.getNickname();
        if(!nickname.isEmpty()&&MessageRequestDto.Enter.ENTER.equals(message.getEnter())){
            message.setMessage(nickname + "님이 들어왔습니다.");
        }
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
        MessageResponseDto result = new MessageResponseDto(time,message);
        sendingOperations.convertAndSend("/api/sub/" + room_id, result);
    }
}
