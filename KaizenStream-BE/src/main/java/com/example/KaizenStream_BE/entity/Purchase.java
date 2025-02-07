package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "purchases")
public class Purchase {
    @Id
    @Column(name = "purchaseID")
    private String purchaseId;

    private String type;
    private double amount;
    private Date purchaseDate;
    private boolean pointReceived;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}
