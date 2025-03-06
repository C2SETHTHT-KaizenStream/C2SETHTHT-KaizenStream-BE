package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String blogId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    User user;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    String content;


    @Column(nullable = false, updatable = false)
    LocalDateTime createAt;

    @Column(nullable = false)
    LocalDateTime updateAt;

    @Column(nullable = false)
    int likeCount = 0;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}