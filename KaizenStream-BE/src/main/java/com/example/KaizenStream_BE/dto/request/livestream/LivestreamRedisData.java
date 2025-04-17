package com.example.KaizenStream_BE.dto.request.livestream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivestreamRedisData implements Serializable {
    private Boolean status;
    private Integer viewCount;
    private Integer duration;
}
