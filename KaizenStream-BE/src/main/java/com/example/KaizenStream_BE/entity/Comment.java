package com.example.KaizenStream_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonBackReference(value = "blog-comments")
    private Blog blog;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    @JsonBackReference(value = "user-comments")
    private User user;

    @Column(nullable = false,columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}