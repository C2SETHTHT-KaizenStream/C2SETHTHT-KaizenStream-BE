package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ChatResponse;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamViewCountRespone;
import com.example.KaizenStream_BE.service.ChatService;
import com.example.KaizenStream_BE.service.LivestreamService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class WebsocketController {

    ChatService chatService;
    SimpMessagingTemplate messagingTemplate;
    LivestreamService livestreamService;
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
        messagingTemplate.convertAndSend("/topic/livestream/chat/" + livestreamId, saved);

        // Lưu tin nhắn vào cơ sở dữ liệu bất đồng bộ (chỉ gọi một lần)
    //    CompletableFuture.runAsync(() -> chatService.saveChatMessage(chatResponse));
    }



    @MessageMapping("/join/chat/{livestreamId}")
    public void joinMessage(@DestinationVariable String livestreamId, String userName) {
        ChatResponse joinMessage = new ChatResponse();
        joinMessage.setMessage(userName + " has joined the chat");
        joinMessage.setUserId("SYSTEM");
        joinMessage.setType("JOIN");
        joinMessage.setLivestreamId(livestreamId);

        ChatResponse saved = chatService.saveChatMessage(joinMessage);
        messagingTemplate.convertAndSend("/topic/livestream/chat/" + livestreamId, saved);
    }

    @MessageMapping("/leave/chat/{livestreamId}")
    public void leaveMessage(@DestinationVariable String livestreamId, String userName) {
        ChatResponse leaveMessage = new ChatResponse();
        leaveMessage.setMessage(userName + " has left the chat");
        leaveMessage.setUserId("SYSTEM");
        leaveMessage.setType("LEAVE");
        leaveMessage.setLivestreamId(livestreamId);
        ChatResponse saved = chatService.saveChatMessage(leaveMessage);
        messagingTemplate.convertAndSend("/topic/livestream/chat/" + livestreamId, saved);
    }





    private RedisTemplate<String, Integer> redisTemplate;
    private void sendViewCount(String livestreamId, String keyViewCount, String keyCurrentViewers) {
        Integer viewCount = redisTemplate.opsForValue().get(keyViewCount);
        Integer currentViewers = redisTemplate.opsForValue().get(keyCurrentViewers);
        LivestreamViewCountRespone respone=new LivestreamViewCountRespone(viewCount,currentViewers);
        // Gửi số lượt xem và số người xem trực tiếp tới tất cả người xem và streamer
        messagingTemplate.convertAndSend("/live/streamer/" + livestreamId, respone);
        messagingTemplate.convertAndSend("/topic/livestream/watch/" + livestreamId, respone);
    }
    @MessageMapping("/join/watch/{livestreamId}")
    public void joinStream(@DestinationVariable String livestreamId) {
        String keyViewCount = "livestream:viewCount:" + livestreamId;
        String keyCurrentViewers = "livestream:currentViewers:" + livestreamId;

        // Tăng số lượt xem tổng thể (viewCount)
        redisTemplate.opsForValue().increment(keyViewCount, 1);

        // Tăng số người xem trực tiếp (currentViewers)
        redisTemplate.opsForValue().increment(keyCurrentViewers, 1);

        // Lấy giá trị mới để gửi cho người dùng
        sendViewCount(livestreamId, keyViewCount, keyCurrentViewers);

        // In log cho việc tham gia
        System.out.println("User joined, livestreamId: " + livestreamId);
        log.warn("User joined, livestreamId: " + livestreamId);
    }


    @MessageMapping("/join/live/{livestreamId}")
    public void startStream(@DestinationVariable String livestreamId) {
        String keyViewCount = "livestream:viewCount:" + livestreamId;
        String keyCurrentViewers = "livestream:currentViewers:" + livestreamId;

        // Kiểm tra và khởi tạo số lượt xem nếu chưa có
        if (!redisTemplate.hasKey(keyViewCount)) {
            redisTemplate.opsForValue().set(keyViewCount, 0);  // Khởi tạo với 0 lượt xem
        }
        if (!redisTemplate.hasKey(keyCurrentViewers)) {
            redisTemplate.opsForValue().set(keyCurrentViewers, 0);  // Khởi tạo với 0 người xem
        }

        // Lấy số lượt xem tổng thể và người xem hiện tại
        sendViewCount(livestreamId, keyViewCount, keyCurrentViewers);
    }
    @MessageMapping("/join/live/stop/{livestreamId}")
    public void stopStream(@DestinationVariable String livestreamId) {
        String keyViewCount = "livestream:viewCount:" + livestreamId;
        Integer viewCount = redisTemplate.opsForValue().get(keyViewCount);

        livestreamService.stopLive(livestreamId, viewCount);
        messagingTemplate.convertAndSend("/watch/stop/" + livestreamId,"Stop Live");


    }

    @MessageMapping("/leave/watch/{livestreamId}")
    public void leaveStream(@DestinationVariable String livestreamId) {
        String keyViewCount = "livestream:viewCount:" + livestreamId;
        String keyCurrentViewers = "livestream:currentViewers:" + livestreamId;

        // Giảm số người xem trực tiếp khi người dùng rời đi
        redisTemplate.opsForValue().decrement(keyCurrentViewers, 1);

        // Lấy số lượt xem hiện tại và số người xem trực tiếp
        sendViewCount(livestreamId, keyViewCount, keyCurrentViewers);


    }





}