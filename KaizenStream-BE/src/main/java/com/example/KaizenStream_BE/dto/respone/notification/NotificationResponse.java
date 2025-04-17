package com.example.KaizenStream_BE.dto.respone.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    String livestreamId;
    String senderAvatar;
    String senderName;
    String content;
    boolean isRead;
    LocalDateTime createAt;
}
