package com.example.KaizenStream_BE.dto.respone.report;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportFormResponse {
    String reportId;
    String userId;
    String livestreamId;
    String reportType;
    String description;
    List<String> images;
    LocalDateTime createdAt;
}
