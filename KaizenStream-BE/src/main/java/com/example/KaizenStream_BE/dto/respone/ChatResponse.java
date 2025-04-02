package com.example.KaizenStream_BE.dto.respone;

import com.example.KaizenStream_BE.entity.Chat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Data


@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ChatResponse {
    String chatId;
    String message;
    LocalDateTime timestamp;
    String userId;
    String username;
    String livestreamId;
    String type;


}
