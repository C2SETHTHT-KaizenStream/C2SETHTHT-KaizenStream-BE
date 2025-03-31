package com.example.KaizenStream_BE.dto.respone;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeRespone {
    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;
}
