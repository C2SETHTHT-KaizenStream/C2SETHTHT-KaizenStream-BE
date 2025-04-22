package com.example.KaizenStream_BE.entity;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "followers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Người theo dõi (Follower)
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    @JsonIgnore
    private User follower;

    // Người được theo dõi (Following)
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    @JsonIgnore
    private User following;

    private LocalDateTime followedAt;
}
