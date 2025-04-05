package com.example.KaizenStream_BE.dto.respone.donation;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonationNotification {
    String viewerName;  // ID người donate
    String itemName;     //ID của vật phẩm
    Integer amount;       // Số lượng donate
}
