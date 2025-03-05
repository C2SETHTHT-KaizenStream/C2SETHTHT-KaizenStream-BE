package com.example.KaizenStream_BE.entity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @Column(name = "commentID")
    @GeneratedValue(strategy = GenerationType.UUID)

    private String commentId;

    @ManyToOne
    @JoinColumn(name = "blogId", nullable = false)
    private Blog blog;
    private String content;
    private Date createAt;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

}