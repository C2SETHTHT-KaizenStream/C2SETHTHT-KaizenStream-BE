package com.example.KaizenStream_BE.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
    //server application
    // /app/sendMessage
    @MessageMapping("/sendNotification") // api
    @SendTo("/topic/notifications") // gửi tin từ server lên client
    public String sendMessage(String message){

        return message;
    }
}
