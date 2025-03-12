package com.example.KaizenStream_BE.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference(value = "user-blogs")
    User user;

    @Column(nullable = false)
    String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    String content;


    @Column(nullable = false, updatable = false)
    LocalDateTime createAt;

    @Column(nullable = false)
    LocalDateTime updateAt;

    @Column(nullable = false)
    int likeCount = 0;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "blog-comments")
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