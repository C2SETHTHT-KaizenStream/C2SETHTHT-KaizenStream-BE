package com.example.KaizenStream_BE.dto.request.blog;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BlogUpdateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String content;
}
