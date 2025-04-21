package com.example.KaizenStream_BE.entity;


import com.example.KaizenStream_BE.enums.AccountStatus;
import com.example.KaizenStream_BE.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.*;

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


    @Column(columnDefinition = "nvarchar(255)")
    private String userName;

    @Column(columnDefinition = "nvarchar(255)")
    private String password;

    private String email;



    private int point;


    @Column(name = "avatar_img", columnDefinition = "nvarchar(255)")
    private String avatarImg;


    private Date createdAt;

    private Date updatedAt;


//    @Column(columnDefinition = "nvarchar(255)")
//    private String status;




    private int followerCount;
//    @Column(name = "flowing_count", nullable = false)
//    private Integer flowingCount = 0;


    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleID", referencedColumnName = "roleID")
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

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follower> following = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follower> followers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Report> reports;
    @Column(columnDefinition = "nvarchar(255)")
    private String channelName;


    @OneToMany(cascade = CascadeType.ALL)
    private List<Livestream> livestreams;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "user-blogs")
    private List<Blog> blogs;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "ban_until")
    private LocalDateTime banUntil; // Thêm trường banUntil để lưu thời gian kết thúc cấm
}
