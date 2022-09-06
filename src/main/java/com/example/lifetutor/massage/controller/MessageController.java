package com.example.lifetutor.massage.controller;

import com.example.lifetutor.massage.dto.request.MessageRequestDto;
import com.example.lifetutor.massage.dto.response.MessageResponseDto;
import com.example.lifetutor.massage.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final MessageService messageService;
    private final SimpMessageSendingOperations sendingOperations;

    @Autowired
    public MessageController(MessageService messageService, SimpMessageSendingOperations sendingOperations) {
        this.messageService = messageService;
        this.sendingOperations = sendingOperations;
    }

    @MessageMapping("/{room_id}")
    public void message(MessageRequestDto message, @DestinationVariable Long room_id){
        MessageResponseDto result = messageService.message(message, room_id);
        sendingOperations.convertAndSend("/api/sub/" + room_id, result);
    }
}
