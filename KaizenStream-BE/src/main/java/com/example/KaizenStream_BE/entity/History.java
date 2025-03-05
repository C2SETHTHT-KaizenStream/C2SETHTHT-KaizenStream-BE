package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "history")
public class History {
    @Id
    @Column(name = "historyID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String historyId;

    private String action;
    private Date actionTime;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}
