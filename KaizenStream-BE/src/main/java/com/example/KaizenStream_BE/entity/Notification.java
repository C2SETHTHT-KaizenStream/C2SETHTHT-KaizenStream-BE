package com.example.KaizenStream_BE.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
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
    private LocalDateTime createAt;

    private String senderAvatar;
    private String senderName;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "livestream_id")
    @JsonManagedReference // giữ lại để được serialize
    private Livestream livestream;


}