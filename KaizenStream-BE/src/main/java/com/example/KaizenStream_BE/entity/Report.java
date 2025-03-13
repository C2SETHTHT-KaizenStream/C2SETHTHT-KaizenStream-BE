package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "report")
public class Report {
    @Id
    @Column(name = "reportID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String reportId;

    @Column(columnDefinition = "nvarchar(max)")
    private String reason;

    private Date createAt;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}
