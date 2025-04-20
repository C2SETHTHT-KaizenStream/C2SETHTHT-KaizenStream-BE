package com.example.KaizenStream_BE.dto.respone.donation;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonateMessageResponse {
    String chatId;
    String livestreamId;
    String message;
    String type;
    String userId;
    LocalDateTime timestamp;
    String userName;
}
