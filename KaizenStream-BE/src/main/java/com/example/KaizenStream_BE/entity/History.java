package com.example.KaizenStream_BE.entity;

import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)  // Đảm bảo Hibernate sẽ tạo UUID cho cột này
    @Column(name = "historyid", nullable = false)  // Đảm bảo không NULL
    private UUID historyId;

    private String action;
    private Date actionTime;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "livestreamID")
    private Livestream livestream;

    private Integer watchDuration; // Tính bằng giây
}
