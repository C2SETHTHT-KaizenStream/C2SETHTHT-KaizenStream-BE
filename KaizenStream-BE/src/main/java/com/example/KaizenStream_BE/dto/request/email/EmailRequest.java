package com.example.KaizenStream_BE.dto.request.email;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {
    String to;
    String subject;
    String htmlContent;
}
