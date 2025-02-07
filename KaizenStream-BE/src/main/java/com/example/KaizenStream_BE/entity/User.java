package com.example.KaizenStream_BE.entity;


import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "userID")
    private String userId;

    private String userName;
    private String password;
    private String roleID;
    private int point;
    private String channelName;
    private String bankAccountNumber;
    private String bankName;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private String status;
    private String avatarImg;
    private int followerCount;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Purchase> purchases;

    @OneToMany( cascade = CascadeType.ALL)
    private List<History> history;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Donation> donations;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Chat> chats;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Follower> followers;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Report> reports;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Livestream> livestreams;

    // Getters and Setters
}
