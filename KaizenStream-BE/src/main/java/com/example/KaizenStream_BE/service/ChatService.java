package com.example.KaizenStream_BE.service;


import com.example.KaizenStream_BE.dto.respone.ChatResponse;
import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.repository.ChatRepository;
import com.example.KaizenStream_BE.repository.LivestreamRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    ChatRepository chatRepository;
    UserRepository userRepository;
    LivestreamRepository livestreamRepository;
    RedisTemplate<String, ChatResponse> redisTemplate;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    static final String CHAT_KEY = "chat:";

    public ChatResponse prepareChatMessage(ChatResponse chatResponse) {
        if (chatResponse.getTimestamp() == null) {
            chatResponse.setTimestamp(LocalDateTime.now());
        }

        if (chatResponse.getUserId() != null && chatResponse.getUsername() == null) {
            User user = userRepository.findById(chatResponse.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            chatResponse.setUsername(user.getUserName());
        }

//        chatResponse.setType("SYSTEM".equals(chatResponse.getUserId()) ? "SYSTEM" : "USER");
        if (chatResponse.getType() == null) {
            chatResponse.setType("SYSTEM".equals(chatResponse.getUserId()) ? "SYSTEM" : "USER");
        }

        return chatResponse;
    }

    public ChatResponse saveChatMessage(ChatResponse chatResponse) {

        prepareChatMessage(chatResponse);
        String cacheKey = CHAT_KEY + chatResponse.getLivestreamId();
        redisTemplate.opsForList().leftPush(CHAT_KEY + chatResponse.getLivestreamId(), chatResponse);
        redisTemplate.expire(cacheKey, 10, TimeUnit.MINUTES);

        executorService.submit(() -> saveChatToDatabase(chatResponse));

        return chatResponse;
    }

    private void saveChatToDatabase(ChatResponse chatResponse) {
        Chat chat = new Chat();
        chat.setMessage(chatResponse.getMessage());
        chat.setTimestamp(LocalDateTime.now());
        chat.setType(chatResponse.getType());


        if ("SYSTEM".equals(chatResponse.getUserId())) {
            User systemUser = userRepository.findById("SYSTEM")
                    .orElseGet(() -> {
                        User sys = new User();
                        sys.setUserId("SYSTEM");
                        sys.setUserName("System");
                        return userRepository.save(sys);
                    });
            chat.setUser(systemUser);
        } else {
            User user = userRepository.findById(chatResponse.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            chat.setUser(user);
            chatResponse.setUsername(user.getUserName());
        }

        Livestream livestream = livestreamRepository.findById(chatResponse.getLivestreamId())
                .orElseThrow(() -> new RuntimeException("Livestream not found"));
        chat.setLivestream(livestream);

        Chat savedChat = chatRepository.save(chat);

        chatResponse.setChatId(savedChat.getChatId());
        chatResponse.setTimestamp(savedChat.getTimestamp());
        if (!"SYSTEM".equals(chatResponse.getUserId())) {
            chatResponse.setUsername(chat.getUser().getUserName());
        }

    }
    //Lấy tin nhắn theo livestream id với paging
//    @Cacheable(value = "chatMessages", key = "#livestreamId + ':' + #page + ':' + #size")
//    public Page<ChatResponse> getChatMessagesByLivestream(String livestreamId, int page, int size) {
//        // Kiểm tra Redis trước
//        List<ChatResponse> cachedMessages = redisTemplate.opsForList()
//                .range(CHAT_KEY + livestreamId, 0, size - 1);
//        if (cachedMessages != null && !cachedMessages.isEmpty()) {
//            return new PageImpl<>(cachedMessages);
//        }
//
//        // Nếu không có trong Redis, lấy từ DB
//        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
//        Page<Chat> chats = chatRepository.findByLivestream_LivestreamId(livestreamId, pageable);
//        return chats.map(chat -> {
//            ChatResponse dto = new ChatResponse();
//            dto.setChatId(chat.getChatId());
//            dto.setMessage(chat.getMessage());
//            dto.setTimestamp(chat.getTimestamp());
//            dto.setUserId(chat.getUser().getUserId());
//            dto.setLivestreamId(chat.getLivestream().getLivestreamId());
//            dto.setUsername(chat.getUser().getUserName());
//            dto.setType(chat.getUser().getUserId().equals("SYSTEM") ? "SYSTEM" : "USER");
//            return dto;
//        });
//    }
//    @Cacheable(value = "chatMessages", key = "#livestreamId + ':' + #page + ':' + #size")
//    public Page<ChatResponse> getChatMessagesByLivestream(String livestreamId, int page, int size) {
//        List<ChatResponse> cachedMessages = redisTemplate.opsForList().range(CHAT_KEY + livestreamId, 0, size - 1);
//        if (cachedMessages != null && !cachedMessages.isEmpty()) {
//            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
//            return new PageImpl<>(cachedMessages, pageable, cachedMessages.size()); // Thêm pageable và total
//        }
//        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
//        Page<Chat> chats = chatRepository.findByLivestream_LivestreamId(livestreamId, pageable);
//        return chats.map(chat -> {
//            ChatResponse dto = new ChatResponse();
//            dto.setChatId(chat.getChatId());
//            dto.setMessage(chat.getMessage());
//            dto.setTimestamp(chat.getTimestamp());
//            dto.setUserId(chat.getUser().getUserId());
//            dto.setLivestreamId(chat.getLivestream().getLivestreamId());
//            dto.setUsername(chat.getUser().getUserName());
//            dto.setType(chat.getUser().getUserId().equals("SYSTEM") ? "SYSTEM" : "USER");
//            return dto;
//        });
//    }

    public Page<ChatResponse> getChatMessagesByLivestream(String livestreamId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
        Page<Chat> chats = chatRepository.findByLivestream_LivestreamId(livestreamId, pageable);

        return chats.map(chat -> {
            ChatResponse dto = new ChatResponse();
            dto.setChatId(chat.getChatId());
            dto.setMessage(chat.getMessage());
            dto.setTimestamp(chat.getTimestamp());
            dto.setUserId(chat.getUser().getUserId());
            dto.setLivestreamId(chat.getLivestream().getLivestreamId());
            dto.setUsername(chat.getUser().getUserName());
            //dto.setType(chat.getUser().getUserId().equals("SYSTEM") ? "SYSTEM" : "USER");
            dto.setType(chat.getType());
            return dto;
        });
    }

}
