package com.example.KaizenStream_BE.entity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "follower")
public class Follower {
    @Id
    @Column(name = "followerID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String followerId;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}