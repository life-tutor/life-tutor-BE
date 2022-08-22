package com.example.lifetutor.massage.service;

import com.example.lifetutor.massage.dto.request.MessageRequestDto;
import com.example.lifetutor.massage.dto.response.MessageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Transactional
@Service
public class MessageService {
    private final SimpMessageSendingOperations sendingOperations;

    @Autowired
    public MessageService(SimpMessageSendingOperations sendingOperations) {
        this.sendingOperations = sendingOperations;
    }

    // 채팅 메시지
    public void message(MessageRequestDto message, Long room_id){
//        foundRoom(room_id);
//        Message msg = new Message(message,user,room);
//        room.addMessage(msg);
        String nickname = message.getNickname();
        if(MessageRequestDto.Enter.ENTER.equals(message.getEnter())){
            message.setMessage(nickname + "님이 들어왔습니다.");
            log.info("enter: "+message.getMessage());
        }
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
        MessageResponseDto result = new MessageResponseDto(time,message);
        sendingOperations.convertAndSend("/api/sub/" + room_id, result);
    }
}
