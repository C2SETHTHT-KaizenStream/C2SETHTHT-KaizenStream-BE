package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chatid")
    private String chatId;

    @Column(columnDefinition = "nvarchar(max)")
    private String message;

    private LocalDateTime timestamp;


    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "livestreamsID", nullable = false)
    private Livestream livestream;
}
