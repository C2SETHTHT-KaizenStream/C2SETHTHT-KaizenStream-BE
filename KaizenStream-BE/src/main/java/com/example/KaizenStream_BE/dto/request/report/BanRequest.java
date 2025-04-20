package com.example.KaizenStream_BE.dto.request.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BanRequest {
    String banReason;
    LocalDateTime banDuration;
}
