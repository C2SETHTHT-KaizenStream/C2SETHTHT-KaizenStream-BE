package com.example.KaizenStream_BE.dto.respone.livestream;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

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
     int viewerCount;

    Date endTime;
    String status;
    private List<String> categories;
    private List<String> tags;
}
