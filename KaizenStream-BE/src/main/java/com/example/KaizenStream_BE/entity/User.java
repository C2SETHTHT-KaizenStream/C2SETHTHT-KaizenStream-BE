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
    @Column(name = "userID", columnDefinition = "nvarchar(255)")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Column(columnDefinition = "nvarchar(255)")
    private String userName;

    @Column(columnDefinition = "nvarchar(255)")
    private String password;

    private int point;

    @Column(columnDefinition = "nvarchar(255)")
    private String channelName;

    @Column(columnDefinition = "nvarchar(255)")
    private String bankAccountNumber;

    @Column(columnDefinition = "nvarchar(255)")
    private String bankName;

    @Column(columnDefinition = "nvarchar(255)")
    private String description;

    private Date createdAt;
    private Date updatedAt;

    @Column(columnDefinition = "nvarchar(255)")
    private String status;

    @Column(columnDefinition = "nvarchar(255)")
    private String avatarImg;

    private int followerCount;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleID")
    )
    private List<Role> roles;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Purchase> purchases;

    @OneToMany(cascade = CascadeType.ALL)
    private List<History> history;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-comments")
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Donation> donations;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Chat> chats;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Follower> followers;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Report> reports;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Livestream> livestreams;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "user-blogs")
    private List<Blog> blogs;

}
