package com.example.KaizenStream_BE.dto.respone.donation;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonationNotification {
    String viewerId;  // ID người donate
    String itemId;     //ID của vật phẩm
    Integer amount;       // Số lượng donate
}
