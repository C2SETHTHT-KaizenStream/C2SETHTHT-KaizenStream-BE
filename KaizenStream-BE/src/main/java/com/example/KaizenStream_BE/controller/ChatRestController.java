package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.respone.ChatResponse;
import com.example.KaizenStream_BE.service.ChatService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ChatRestController {

    final ChatService  chatService;




//@GetMapping("/livestream/{livestreamId}")
//public Page<ChatResponse> getChatMessages(
//        @PathVariable String livestreamId,
//        @RequestParam(defaultValue = "0") int page,
//        @RequestParam(defaultValue = "20") int size) {
//    return chatService.getChatMessagesByLivestream(livestreamId, page, size);
//}

//    @GetMapping("/livestream/{livestreamId}")
//    public List<ChatResponse> getChatMessages(
//            @PathVariable String livestreamId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        Page<ChatResponse> chatMessages = chatService.getChatMessagesByLivestream(livestreamId, page, size);
//        return chatMessages.getContent();
//    }

    @GetMapping("/livestream/{livestreamId}")
    public List<ChatResponse> getChatMessages(
            @PathVariable String livestreamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChatResponse> chatMessages = chatService.getChatMessagesByLivestream(livestreamId, page, size);
        return chatMessages.getContent();
    }


}