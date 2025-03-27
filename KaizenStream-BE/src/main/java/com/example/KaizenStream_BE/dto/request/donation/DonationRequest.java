package com.example.KaizenStream_BE.dto.request.donation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonationRequest {
    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Livestream ID cannot be blank")
    private String livestreamId;

    @NotBlank(message = "Item ID cannot be blank")
    private String itemId;

    @Min(value = 1, message = "Amount must be at least 1")
    private Integer amount;
}
