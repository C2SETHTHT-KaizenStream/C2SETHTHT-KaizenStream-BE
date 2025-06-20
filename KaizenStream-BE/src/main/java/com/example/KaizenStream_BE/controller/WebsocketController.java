package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ChatResponse;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamViewCountRespone;
import com.example.KaizenStream_BE.service.ChatService;
import com.example.KaizenStream_BE.service.HistoryService;
import com.example.KaizenStream_BE.service.LivestreamRedisService;
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
    LivestreamRedisService livestreamRedisService;
    private final HistoryService historyService;
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

        ChatResponse saved = chatService.saveChatMessage(chatResponse);

        messagingTemplate.convertAndSend("/topic/livestream/chat/" + livestreamId, saved);


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

    private void sendViewCount(String livestreamId, String keyViewCount, String keyCurrentViewers, Boolean isLive) {
        Integer currentViewers;
        if(keyCurrentViewers!="-1")   currentViewers = redisTemplate.opsForValue().get(keyCurrentViewers);
        else currentViewers=0;
        Integer viewCount = redisTemplate.opsForValue().get(keyViewCount);

        LivestreamViewCountRespone respone=new LivestreamViewCountRespone(viewCount,currentViewers);

        if (isLive) messagingTemplate.convertAndSend("/live/streamer/" + livestreamId, respone);
        messagingTemplate.convertAndSend("/topic/livestream/watch/" + livestreamId, respone);
    }

    @MessageMapping("/join/watch/{livestreamId}/{userId}")
    public void joinStream(@DestinationVariable String livestreamId, @DestinationVariable String userId) {
        String keyViewCount = "livestream:viewCount:" + livestreamId;
        String keyCurrentViewers = "livestream:currentViewers:" + livestreamId;
        System.out.println("{userId}"+userId);
        log.warn("{userId}"+userId);

        // Tăng số lượt xem tổng thể (viewCount)
        redisTemplate.opsForValue().increment(keyViewCount, 1);

        // Tăng số người xem trực tiếp (currentViewers)
        redisTemplate.opsForValue().increment(keyCurrentViewers, 1);

        // Lấy giá trị mới để gửi cho người dùng
        sendViewCount(livestreamId, keyViewCount, keyCurrentViewers, true);

        // Gọi HistoryService để lưu thông tin lịch sử khi người dùng tham gia livestream
        historyService.saveViewHistory(userId, livestreamId, 0);  // Ban đầu, thời gian xem là 0 giây

        // In log cho việc tham gia
        System.out.println("User joined, livestreamId: " + livestreamId);
        log.warn("User joined, livestreamId: " + livestreamId);
    }


    @MessageMapping("/join/watch/vod/{livestreamId}/{userId}")
    public void joinVodStream(@DestinationVariable String livestreamId, @DestinationVariable String userId) {
        String keyViewCount = "vod:viewCount:" + livestreamId;


        if (!redisTemplate.hasKey(keyViewCount)) {
            log.warn("vod:viewCount "+keyViewCount+"\n"+ redisTemplate.opsForValue().get(keyViewCount));
            redisTemplate.opsForValue().increment(keyViewCount, 1);

        } else {
            redisTemplate.opsForValue().set(keyViewCount, 1);
            log.warn("vod:viewCount "+keyViewCount+"\n"+ redisTemplate.opsForValue().get(keyViewCount));

        }
        sendViewCount(livestreamId, keyViewCount, "-1", false);
        livestreamRedisService.saveOrUpdateViewCounts(livestreamId,redisTemplate.opsForValue().get(keyViewCount),false);
      //  historyService.saveViewHistory(userId, livestreamId, 0);

    }

    @MessageMapping("/join/live/{livestreamId}/{userId}")
    public void startStream(@DestinationVariable String livestreamId, @DestinationVariable String userId) {
        String keyViewCount = "livestream:viewCount:" + livestreamId;
        String keyCurrentViewers = "livestream:currentViewers:" + livestreamId;
        String streamerKey="streamer:"+userId;
        // Kiểm tra và khởi tạo số lượt xem nếu chưa có
        if (!redisTemplate.hasKey(keyViewCount)) {
            redisTemplate.opsForValue().set(keyViewCount, 0);  // Khởi tạo với 0 lượt xem

        }
        if (!redisTemplate.hasKey(keyCurrentViewers)) {
            redisTemplate.opsForValue().set(keyCurrentViewers, 0);  // Khởi tạo với 0 người xem
        }
        if(!redisTemplate.hasKey(streamerKey)){
            redisTemplate.opsForValue().set(streamerKey,0);
        }
        sendViewCount(livestreamId, keyViewCount, keyCurrentViewers,true);
    }
    @MessageMapping("/join/live/stop/{livestreamId}/{userId}")
    public void stopStream(@DestinationVariable String livestreamId, @DestinationVariable String userId) {

        String keyViewCount = "livestream:viewCount:" + livestreamId;
        Integer viewCount = redisTemplate.opsForValue().get(keyViewCount);
        String keyCurrentViewers = "livestream:currentViewers:" + livestreamId;

        String streamerKey="streamer:"+userId;
        redisTemplate.delete(streamerKey);
        if(viewCount>=0) {
            redisTemplate.delete(keyViewCount);
            redisTemplate.delete(keyCurrentViewers);
            log.warn("stopLivestopLivestopLivestopLivestopLive: " + viewCount);
            log.warn("set viewcount: " + viewCount);

            livestreamRedisService.saveOrUpdateViewCounts(livestreamId,viewCount,true);
            messagingTemplate.convertAndSend("/watch/stop/" + livestreamId, "Stop Live");
        }

    }

    @MessageMapping("/leave/watch/{livestreamId}")
    public void leaveStream(@DestinationVariable String livestreamId) {
        String keyViewCount = "livestream:viewCount:" + livestreamId;
        String keyCurrentViewers = "livestream:currentViewers:" + livestreamId;

        // Giảm số người xem trực tiếp khi người dùng rời đi
        redisTemplate.opsForValue().decrement(keyCurrentViewers, 1);

        // Lấy số lượt xem hiện tại và số người xem trực tiếp
        sendViewCount(livestreamId, keyViewCount, keyCurrentViewers,true);

    }
    @MessageMapping("/watch/report/{livestreamId}")
    public void handleReportStream(@DestinationVariable String livestreamId) {
        messagingTemplate.convertAndSend("/live/banned/" + livestreamId, "Your live stream has been forcibly stopped for violating some platform regulations.");

    }





}