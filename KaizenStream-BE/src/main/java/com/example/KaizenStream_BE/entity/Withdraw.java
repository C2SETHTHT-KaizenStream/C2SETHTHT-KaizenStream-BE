package com.example.KaizenStream_BE.entity;

import com.example.KaizenStream_BE.enums.WithdrawStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdraws")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Withdraw {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "withdrawId")
    private String withdrawId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "userID", nullable = false) // Mapping chính xác theo DB hiện tại
    private User user;

    private int pointsRequested;

    private double usdExpected;

    @Column(columnDefinition = "nvarchar(255)")
    private String bankName;

    @Column(columnDefinition = "nvarchar(255)")
    private String bankAccount;

    @Column(columnDefinition = "nvarchar(max)")
    private String note;

    @Enumerated(EnumType.STRING)
    private WithdrawStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (status == null) status = WithdrawStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
