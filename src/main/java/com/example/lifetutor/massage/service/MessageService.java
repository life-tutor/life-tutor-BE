package com.example.lifetutor.massage.service;

import com.example.lifetutor.massage.dto.request.MessageRequestDto;
import com.example.lifetutor.massage.dto.response.MessageResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class MessageService {

    // 채팅 메시지
    public MessageResponseDto message(MessageRequestDto message, Long room_id){
        String nickname = message.getNickname();
        if(MessageRequestDto.Enter.ENTER.equals(message.getEnter())){
            message.setMessage(nickname + "님이 들어왔습니다.");
        }
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
        return new MessageResponseDto(time,message);
    }
}
