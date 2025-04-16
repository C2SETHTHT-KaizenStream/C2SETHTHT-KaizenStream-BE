package com.example.KaizenStream_BE.dto.respone.blogLike;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlogLikeResponse {
    String message;
    boolean liked;
    int likeCount;
}