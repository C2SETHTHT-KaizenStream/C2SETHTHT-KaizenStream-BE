package com.example.KaizenStream_BE.dto.respone.donation;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewerNotification {
    String streamerId;  // ID của streamer
    String viewerId;    // ID người donate
    String viewerName;  // ID người donate
    String itemName;     //ID của vật phẩm
    String itemImage;    // Ảnh của vật phẩm
    Integer amount;       // Số lượng donate
}
