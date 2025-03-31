package com.example.KaizenStream_BE.dto.respone.schedule;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class CreateScheduleRespone {
     String scheduleId;
     String description;
     Date scheduleTime;
     String status;
}
