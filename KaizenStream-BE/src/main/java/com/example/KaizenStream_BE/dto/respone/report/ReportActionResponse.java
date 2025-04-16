package com.example.KaizenStream_BE.dto.respone.report;

import com.example.KaizenStream_BE.enums.ReportStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportActionResponse {
    String reportId;
    ReportStatus status;
}
