package com.example.KaizenStream_BE.entity;

import com.example.KaizenStream_BE.enums.Status;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "livestreams")
public class Livestream {
    @Id
    @Column(name = "livestreamsID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String livestreamId;

    @Column(columnDefinition = "nvarchar(max)")

    private String title;
    @Column(columnDefinition = "nvarchar(max)")

    private String description;
    private String thumbnail;
    private int viewerCount;
    @Column(nullable = true, columnDefinition = "int default 0")
    private int duration; // Duration of the video in seconds

    private Date startTime;
    private Date endTime;

    private String status= Status.INACTIVE.getDescription();

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @OneToMany(mappedBy = "livestream", cascade = CascadeType.ALL)
    private List<Donation> donations;

    @OneToMany(mappedBy = "livestream", cascade = CascadeType.ALL)
    private List<Chat> chats;

    @ManyToMany
    @JoinTable(
            name = "livestream_categories", // Tên bảng liên kết
            joinColumns = @JoinColumn(name = "livestreamsID"), // Cột liên kết cho Livestream
            inverseJoinColumns = @JoinColumn(name = "categoryId") // Cột liên kết cho Category
    )
    private List<Category> categories;
    @OneToOne
    @JoinColumn(name = "scheduleID",nullable = true)
    private Schedule schedule;


    // Quan hệ nhiều-nhiều với Tag
    @ManyToMany
    @JoinTable(
            name = "livestream_tag",
            joinColumns = @JoinColumn(name = "livestream_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @OneToMany(mappedBy = "stream", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Report> reports;


}
