package com.example.KaizenStream_BE.dto.respone.suggestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionResponse {
    private String livestreamId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String streamUrl;
    private int viewerCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String streamerId;
    private String streamerName;
    private Set<String> tags;
    private Set<String> categories;
    private double score;
}
