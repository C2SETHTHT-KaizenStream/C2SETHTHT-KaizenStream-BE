package com.example.KaizenStream_BE.dto.respone.livestream;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@Builder
public class LivestreamRespone {
    String livestreamId;
    String title;
    String description;
    String thumbnail;
    Date startTime;
    int viewerCount;

    Date endTime;
    String status;
    int duration;
    String streamerId;
    String streamerImgUrl;
    String channelName;
    private List<String> categories;
    private List<String> tags;
}
