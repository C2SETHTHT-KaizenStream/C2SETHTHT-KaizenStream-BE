package com.example.KaizenStream_BE.dto.request.purchase;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseRequest {

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    private String type;

    @Min(value = 1, message = "Amount must be at least 1")
    private double amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")  // Đảm bảo ngày tháng theo định dạng ISO
    private LocalDateTime purchaseDate;

    private int pointReceived;
}