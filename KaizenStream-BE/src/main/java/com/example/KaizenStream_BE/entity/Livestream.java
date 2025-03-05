package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "livestreams")
public class Livestream {
    @Id
    @Column(name = "livestreamsID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String livestreamId;

    private String title;
    private String description;
    private String thumbnail;
    private int viewerCount;
    private Date startTime;
    private Date endTime;
    private String status;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @OneToMany(mappedBy = "livestream", cascade = CascadeType.ALL)
    private List<Donation> donations;

    @OneToMany(mappedBy = "livestream", cascade = CascadeType.ALL)
    private List<Chat> chats;
}
