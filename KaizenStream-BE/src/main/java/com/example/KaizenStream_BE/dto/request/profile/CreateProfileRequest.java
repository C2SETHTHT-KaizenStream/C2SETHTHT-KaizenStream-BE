package com.example.KaizenStream_BE.dto.request.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {
    private String userId;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String bio;
    private String avatarUrl;
    private String gender;
    private Date dateOfBirth;
    private String bankAccountNumber;
    private String bankName;
    private String description;


} 