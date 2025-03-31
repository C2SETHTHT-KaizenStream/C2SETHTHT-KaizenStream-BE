package com.example.KaizenStream_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private String profileId;
    private String userId;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String bio;
    private String avatarUrl;
    private String gender;
    private Date dateOfBirth;
    private Date createdAt;
    private Date updatedAt;
    private String status;
} 