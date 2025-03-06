package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Data
public class Comment {
    @Id
    @Column(name = "commentID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String commentId;

    @ManyToOne
    @JoinColumn(name = "blogId", nullable = false)
    private Blog blog;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}