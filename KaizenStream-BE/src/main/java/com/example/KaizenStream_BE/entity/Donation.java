package com.example.KaizenStream_BE.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "donation")
@Data
public class Donation {
    @Id
    @Column(name = "donationID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String donationId;

    private Integer quantityItems;
    private Integer pointSpent;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "gift_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "livestreamsID", nullable = false)
    private Livestream livestream;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}