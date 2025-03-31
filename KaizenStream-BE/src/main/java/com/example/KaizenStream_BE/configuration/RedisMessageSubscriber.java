//package com.example.KaizenStream_BE.configuration;
//
//import com.example.KaizenStream_BE.dto.respone.ChatResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class RedisMessageSubscriber {
//    private final SimpMessagingTemplate messagingTemplate;
//
//    /**
//     * Xử lý tin nhắn nhận được từ Redis Pub/Sub
//     */
//    public void onMessage(ChatResponse message) {
//        // Gửi tin nhắn tới tất cả client đang subscribe topic của livestream
//        messagingTemplate.convertAndSend("/topic/livestream/" + message.getLivestreamId(), message);
//    }
//}