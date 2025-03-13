package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String chatId;

    @Column(columnDefinition = "nvarchar(max)")
    private String message;

    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "livestreamsID", nullable = false)
    private Livestream livestream;
}
