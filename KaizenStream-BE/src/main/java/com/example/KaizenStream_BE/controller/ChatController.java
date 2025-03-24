package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ChatResponse;
import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {

    ChatService chatService;
    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatResponse sendMessage(ChatResponse chatRequest) {
        Chat saved = chatService.saveChat(chatRequest);

        ChatResponse response = new ChatResponse();
        response.setUserId(saved.getUser().getUserId());
        response.setLivestreamId(saved.getLivestream().getLivestreamId());
        response.setMessage(saved.getMessage());
        response.setTimestamp(saved.getTimestamp());

        return response;
    }


}
