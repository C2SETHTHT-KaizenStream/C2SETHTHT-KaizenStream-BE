package com.example.KaizenStream_BE.controller;

import com.cloudinary.Api;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.notification.NotificationResponse;
import com.example.KaizenStream_BE.dto.respone.report.ReportDetailResponse;
import com.example.KaizenStream_BE.repository.NotificationRepository;
import com.example.KaizenStream_BE.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;
    //server application
    // /app/sendMessage
    @MessageMapping("/sendNotification") // api
    @SendTo("/topic/notifications") // gửi tin từ server lên client
    public String sendMessage(String message){
        return message;
    }

    @GetMapping()
    public ApiResponse<List<NotificationResponse>> getNotificationsByUser(@RequestParam String id){
        return ApiResponse.<List<NotificationResponse>>builder()
                .code(200)
                .message("Get report detail successfully !")
                .result(notificationService.getNotifcationsByUser(id))
                .build();
    }

    @PutMapping("/mark-as-read")
    public ApiResponse<List<NotificationResponse>> markAsRead(@RequestBody List<NotificationResponse> notifications) {
       return ApiResponse.<List<NotificationResponse>>builder()
               .code(200)
               .message("Mark as read successfully !")
               .result(notificationService.markAsRead(notifications))
               .build();
    }
}
