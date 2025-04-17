package com.example.KaizenStream_BE.dto.request.blog;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogCreateRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;
}
