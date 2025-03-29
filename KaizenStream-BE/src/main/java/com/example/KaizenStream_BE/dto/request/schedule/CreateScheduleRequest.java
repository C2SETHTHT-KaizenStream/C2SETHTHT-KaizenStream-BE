package com.example.KaizenStream_BE.dto.request.schedule;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CreateScheduleRequest {
    @NotEmpty(message = "Description is required")
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @NotNull(message = "Schedule time is required")
    private Date scheduleTime;

    private String userId;  // Liên kết với User
}
