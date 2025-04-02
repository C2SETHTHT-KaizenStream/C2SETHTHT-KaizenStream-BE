package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "purchases")
public class Purchase {
    @Id
    @Column(name = "purchaseID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String purchaseId;

    private String type;
    private double amount;
    private LocalDateTime purchaseDate;
    private int pointReceived;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}
