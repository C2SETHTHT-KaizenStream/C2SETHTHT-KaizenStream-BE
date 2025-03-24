package com.example.KaizenStream_BE.dto.respone;

import lombok.Data;

import java.util.Date;

@Data
public class ChatResponse {
    private String userId;
    private String livestreamId;
    private String message;
    private Date timestamp;
}
