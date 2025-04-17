package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.respone.BlogResponse;
import com.example.KaizenStream_BE.dto.respone.channel.ChannelResponse;
import com.example.KaizenStream_BE.dto.respone.search.SearchResultResponse;
import com.example.KaizenStream_BE.service.BlogService;
import com.example.KaizenStream_BE.service.ChannelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/search")
@RequiredArgsConstructor

public class SearchController {
    BlogService blogService;
    ChannelService channelService;

    @GetMapping
    public ResponseEntity<SearchResultResponse> searchAll(@RequestParam String query,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        List<BlogResponse> blogs = blogService.searchBlogs(query, page, size).getContent();
        List<ChannelResponse> channel = channelService.searchChannels(query, page, size).getContent();



        SearchResultResponse result = SearchResultResponse.builder()
                .blogResponseList(blogs)
                .channelResponseList(channel)

                .build();

        return ResponseEntity.ok(result);
    }
}
