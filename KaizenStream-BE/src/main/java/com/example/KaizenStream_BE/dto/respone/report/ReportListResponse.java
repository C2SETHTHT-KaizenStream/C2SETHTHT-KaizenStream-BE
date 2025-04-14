package com.example.KaizenStream_BE.dto.respone.report;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportListResponse {
    String reportId;
    String reportType;
    String description;
    LocalDateTime createdAt;
    String streamerName;
    ReportStatus status;
    String userName;
}
