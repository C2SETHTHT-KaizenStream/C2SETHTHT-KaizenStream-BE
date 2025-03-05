package com.example.KaizenStream_BE.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String blogId;
    @ManyToOne
    @JoinColumn(name = "userId",nullable = false)
    User user;
    String title;
    String content;
    Date createAt;
    Date updateAt;

}
