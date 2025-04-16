package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "leaderboard")
public class Leaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String  id;
    // Mối quan hệ với bảng Users (nhiều leaderboard -> 1 user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_viewers", nullable = false)
    private Integer totalViewers = 0;

    @Column(name = "total_donations", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalDonations = BigDecimal.ZERO;

    @Column(name = "type", length = 50)
    private String type; // "weekly", "monthly", "all-time", v.v.

    @Column(name = "time_frame_start")
    private LocalDate timeFrameStart;

    @Column(name = "time_frame_end")
    private LocalDate timeFrameEnd;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();


}
