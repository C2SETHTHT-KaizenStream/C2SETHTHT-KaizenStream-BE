package com.example.KaizenStream_BE.dto.respone.report;

import com.example.KaizenStream_BE.enums.ReportStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportDetailResponse {
    String description;
    LocalDateTime createdAt;
    String streamerName;
    String userName;
    List<String> images;
}
