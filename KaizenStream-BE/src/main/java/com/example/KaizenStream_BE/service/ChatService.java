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
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    ChatRepository chatRepository;
    UserRepository repository;
    LivestreamRepository livestreamRepository;


    public Chat saveChat(ChatResponse chatResponse)
    {
        User user = repository.findById(chatResponse.getUserId()).orElseThrow();
        Livestream livestream = livestreamRepository.findById(chatResponse.getLivestreamId()).orElseThrow();

        Chat chat = new Chat();
        chat.setMessage(chatResponse.getMessage());
        chat.setUser(user);
        chat.setLivestream(livestream);
        chat.setTimestamp(new Date());

        return chatRepository.save(chat);
    }

}
