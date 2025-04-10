package com.example.KaizenStream_BE.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionRequest {
    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotNull(message = "Limit cannot be null")
    @Positive(message = "Limit must be positive")
    private Integer limit;
}
