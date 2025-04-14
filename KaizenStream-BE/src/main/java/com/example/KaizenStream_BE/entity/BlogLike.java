package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@IdClass(BlogLikeId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogLike {
    @Id
    String userId;

    @Id
    String blogId;

    @ManyToOne
    @JoinColumn(name = "blogId", insertable = false, updatable = false)
    Blog blog;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    User user;

    LocalDateTime likedAt;
}
