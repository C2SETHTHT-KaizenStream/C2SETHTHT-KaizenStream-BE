package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @Column(name = "notificationID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String notificationId;

    @Column(columnDefinition = "nvarchar(max)")
    private String content;
    private boolean isRead;
    private Date createAt;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}