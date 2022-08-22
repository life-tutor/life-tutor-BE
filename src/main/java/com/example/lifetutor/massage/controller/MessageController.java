package com.example.lifetutor.massage.controller;

import com.example.lifetutor.massage.dto.request.MessageRequestDto;
import com.example.lifetutor.massage.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/{room_id}")
    public void message(@RequestBody MessageRequestDto message, @DestinationVariable Long room_id){
        messageService.message(message, room_id);
    }
}