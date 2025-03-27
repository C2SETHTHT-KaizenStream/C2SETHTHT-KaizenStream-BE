package com.example.KaizenStream_BE.entity;

import com.example.KaizenStream_BE.enums.StatusItem;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "item")
public class Item {
    @Id
    @Column(name = "itemId")
    @GeneratedValue(strategy = GenerationType.UUID)
    String itemId;

    @Column(nullable = false, length = 255)
    String name; // Tên item

    @Column(columnDefinition = "TEXT")
    String description; // Mô tả item donation

    @Column(nullable = false)
    String price; // Giá của item (Coins)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatusItem status; // Trạng thái item

    @Column(nullable = true)
    String image;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt; // Thời gian tạo item

    @UpdateTimestamp
    LocalDateTime updatedAt; // Thời gian cập nhật gần nhất

}
