package com.example.KaizenStream_BE.dto.request.chat;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRequest {
    private String userId;
    private String message;
}
