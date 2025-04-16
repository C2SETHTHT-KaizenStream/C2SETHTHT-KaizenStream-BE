package com.example.KaizenStream_BE.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank (message = "content is required")
    private String content;

    @NotBlank (message = "userId is required")
    private String userId;
}
