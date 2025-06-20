package com.example.KaizenStream_BE.dto.respone.search;


import com.example.KaizenStream_BE.dto.respone.BlogResponse;
import com.example.KaizenStream_BE.dto.respone.channel.ChannelResponse;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamRespone;
import com.example.KaizenStream_BE.entity.Blog;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class SearchResultResponse {
    List<BlogResponse> blogResponseList;
    List<ChannelResponse> channelResponseList;
    List <LivestreamRespone> livestreamResponseList;


    private long totalChannels;
    private long totalLivestreams;
    private int currentPage;
    private int pageSize;

}
