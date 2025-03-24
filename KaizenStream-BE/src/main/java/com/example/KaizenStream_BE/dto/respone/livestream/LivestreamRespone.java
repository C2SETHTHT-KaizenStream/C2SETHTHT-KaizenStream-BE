package com.example.KaizenStream_BE.dto.respone.livestream;

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
public class LivestreamRespone {
    String livestreamId;
    String title;
    String description;
    String thumbnail;
    Date startTime;
    Date endTime;
    String status;
}
