package com.example.KaizenStream_BE.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "userID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String userId;

    private String userName;
    private String password;
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleID")
    )
    private  List<Role> roles;


    @OneToMany( cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany( cascade = CascadeType.ALL)
    private List<Purchase> purchases;

    @OneToMany( cascade = CascadeType.ALL)
    private List<History> history;

    @OneToMany( cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-comments")
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


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "user-blogs")
    private List<Blog> blogs;

}
