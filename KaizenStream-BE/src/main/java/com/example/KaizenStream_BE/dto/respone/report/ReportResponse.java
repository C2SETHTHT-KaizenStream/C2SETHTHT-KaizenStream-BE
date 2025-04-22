package com.example.KaizenStream_BE.dto.respone.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResponse {
    String reportId;
    String userAvatar;
    String userName;       // Tên của người gửi báo cáo
    LocalDateTime timestamp; // Thời gian gửi báo cáo
}
