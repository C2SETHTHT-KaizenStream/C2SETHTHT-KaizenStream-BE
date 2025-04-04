package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ChatResponse;
import com.example.KaizenStream_BE.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {

    ChatService chatService;
    SimpMessagingTemplate messagingTemplate;

//        @MessageMapping("/chat/{livestreamId}")
//    public void sendMessage(@DestinationVariable String livestreamId, ChatResponse chatResponse) {
//        chatResponse.setLivestreamId(livestreamId);
//            //chatService.prepareChatMessage(chatResponse);
////            chatResponse.setLivestreamId(livestreamId);
//        ChatResponse saved = chatService.saveChatMessage(chatResponse);
//        messagingTemplate.convertAndSend("/topic/livestream/" + livestreamId, saved);
//            CompletableFuture.runAsync(() -> chatService.saveChatMessage(chatResponse));
//    }
@MessageMapping("/chat/{livestreamId}")
public void sendMessage(@DestinationVariable String livestreamId, ChatResponse chatResponse) {
    chatResponse.setLivestreamId(livestreamId);

    // Lưu tin nhắn đồng bộ lần đầu tiên
    ChatResponse saved = chatService.saveChatMessage(chatResponse);

    // Gửi tin nhắn qua WebSocket
    messagingTemplate.convertAndSend("/topic/livestream/" + livestreamId, saved);

}



    @MessageMapping("/join/{livestreamId}")
    public void joinMessage(@DestinationVariable String livestreamId, String userName) {
        ChatResponse joinMessage = new ChatResponse();
        joinMessage.setMessage(userName + " has joined the chat");
        joinMessage.setUserId("SYSTEM");
        joinMessage.setType("JOIN");
        joinMessage.setLivestreamId(livestreamId);


        ChatResponse saved = chatService.saveChatMessage(joinMessage);
        messagingTemplate.convertAndSend("/topic/livestream/" + livestreamId, saved);
    }

    @MessageMapping("/leave/{livestreamId}")
    public void leaveMessage(@DestinationVariable String livestreamId, String userName) {
        ChatResponse leaveMessage = new ChatResponse();
        leaveMessage.setMessage(userName + " has left the chat");
        leaveMessage.setUserId("SYSTEM");
        leaveMessage.setType("LEAVE");
        leaveMessage.setLivestreamId(livestreamId);


        ChatResponse saved = chatService.saveChatMessage(leaveMessage);
        messagingTemplate.convertAndSend("/topic/livestream/" + livestreamId, saved);
    }
}