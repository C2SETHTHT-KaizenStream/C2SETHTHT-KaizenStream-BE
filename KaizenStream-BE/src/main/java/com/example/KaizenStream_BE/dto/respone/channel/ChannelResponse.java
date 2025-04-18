package com.example.KaizenStream_BE.dto.respone.channel;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelResponse {
    String userId;
    String userName;
    String channelName;
}
