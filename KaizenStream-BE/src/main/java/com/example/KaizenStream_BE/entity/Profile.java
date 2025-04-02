package com.example.KaizenStream_BE.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @Column(name = "profileID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String profileId;

    @OneToOne
    @JoinColumn(name = "userID", referencedColumnName = "userID")
    private User user;

    @Column(columnDefinition = "nvarchar(255)")
    private String fullName;

    @Column(columnDefinition = "nvarchar(255)")
    private String phoneNumber;

    @Column(columnDefinition = "nvarchar(255)")
    private String address;

    @Column(columnDefinition = "nvarchar(255)")
    private String bio;

    private String avatarUrl;

    @Column(columnDefinition = "nvarchar(255)")
    private String gender;

    private Date dateOfBirth;




    @Column(columnDefinition = "nvarchar(255)")
    private String bankAccountNumber;

    @Column(columnDefinition = "nvarchar(255)")
    private String bankName;

    private String description;


} 