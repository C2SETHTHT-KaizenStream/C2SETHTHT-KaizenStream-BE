package com.example.KaizenStream_BE.dto.request.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class ReportRequest {
    String userId;       // ID của người gửi báo cáo
    LocalDateTime timestamp; // Thời gian gửi báo cáo
}
