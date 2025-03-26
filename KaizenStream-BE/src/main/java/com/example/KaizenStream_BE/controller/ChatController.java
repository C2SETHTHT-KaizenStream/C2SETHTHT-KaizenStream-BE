package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ChatResponse;
import com.example.KaizenStream_BE.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * The type Chat controller.
 */
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {

    ChatService chatService;
    SimpMessagingTemplate messagingTemplate;


//    @MessageMapping("/chat/{livestreamId}")
//    @SendTo("/topic/livestream/{livestreamId}")
//    public ChatResponse sendMessage(@DestinationVariable String livestreamId, ChatResponse chatResponse) {
//        chatResponse.setLivestreamId(livestreamId);
//        return chatService.saveChatMessage(chatResponse);
//    }

    /**
     * Send message.
     *
     * @param livestreamId the livestream id
     * @param chatResponse the chat response
     */
    @MessageMapping("/chat/{livestreamId}")
    public void sendMessage(@DestinationVariable String livestreamId, ChatResponse chatResponse) {
        chatResponse.setLivestreamId(livestreamId);
        ChatResponse saved = chatService.saveChatMessage(chatResponse);
        messagingTemplate.convertAndSend("/topic/livestream/" + livestreamId, saved);
    }

    @MessageMapping("/leaveMessage/{livestreamId}")
    public void leaveMessage(@DestinationVariable String livestreamId, String userName) {
        ChatResponse leaveMessage = new ChatResponse();
        leaveMessage.setMessage(userName + " has left the chat");
        leaveMessage.setUserId("SYSTEM");
        leaveMessage.setType("LEAVE");
        leaveMessage.setLivestreamId(livestreamId);

        ChatResponse saved = chatService.saveChatMessage(leaveMessage); // Lưu vào database
        messagingTemplate.convertAndSend("/topic/livestream/" + livestreamId + "/message", saved);
    }

    @MessageMapping("/joinMessage/{livestreamId}")
    public void joinMessage(@DestinationVariable String livestreamId, String userName) {
        ChatResponse joinMessage = new ChatResponse();
        joinMessage.setMessage(userName + " has joined the chat");
        joinMessage.setUserId("SYSTEM");
        joinMessage.setType("JOIN");
        joinMessage.setLivestreamId(livestreamId);

        ChatResponse saved = chatService.saveChatMessage(joinMessage); // Lưu vào database
        messagingTemplate.convertAndSend("/topic/livestream/" + livestreamId + "/message", saved);
    }






}
