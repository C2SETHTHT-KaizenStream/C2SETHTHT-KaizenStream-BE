package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "report")
public class Report {
    @Id
    @Column(name = "reportID")
    private String reportId;

    private String reason;
    private Date createAt;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}
