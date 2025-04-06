package com.example.KaizenStream_BE.dto.respone.livestream;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class LivestreamViewCountRespone {
    private int viewCount;
    private int currentViewers;
}
