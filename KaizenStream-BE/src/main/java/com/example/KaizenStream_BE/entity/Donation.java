package com.example.KaizenStream_BE.entity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "donation")
public class Donation {
    @Id
    @Column(name = "donationID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String donationId;

    private int quantityItems;
    private int pointSpent;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "livestreamsID", nullable = false)
    private Livestream livestream;
}