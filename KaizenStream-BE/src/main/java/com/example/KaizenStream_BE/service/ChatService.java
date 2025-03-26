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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    ChatRepository chatRepository;
    UserRepository userRepository;
    LivestreamRepository livestreamRepository;

    // Lưu tin nhắn
    public ChatResponse saveChatMessage(ChatResponse chatResponse) {
        Chat chat = new Chat();
        chat.setMessage(chatResponse.getMessage());
        chat.setTimestamp(LocalDateTime.now());

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
        return chatResponse;
    }

    // Lấy tin nhắn theo livestream id với paging
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
            dto.setType(chat.getUser().getUserId().equals("SYSTEM") ? "SYSTEM" : "USER");
            return dto;
        });
    }
}
