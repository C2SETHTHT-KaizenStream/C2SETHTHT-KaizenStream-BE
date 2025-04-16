package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String historyId;

    private String action;
    private Date actionTime;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    // Thêm trường để theo dõi livestream đã xem
    @ManyToOne
    @JoinColumn(name = "livestreamID")
    private Livestream livestream;

    // Thêm trường để lưu thời gian xem
    private Integer watchDuration; // Tính bằng giây
}

