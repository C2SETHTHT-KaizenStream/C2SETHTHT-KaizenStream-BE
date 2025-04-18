package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.channel.ChannelResponse;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.mapper.UserMapper;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class ChannelService {
    UserRepository userRepository;
    UserMapper userMapper;

    public Page<ChannelResponse> searchChannels(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findByChannelNameContainingIgnoreCase(query, pageable);
        return userPage.map(userMapper::toChannelResponse);
    }

}
