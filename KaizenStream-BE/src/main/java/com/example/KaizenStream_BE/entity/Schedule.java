package com.example.KaizenStream_BE.entity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @Column(name = "scheduleID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String scheduleId;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;
    private Date scheduleTime;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}
