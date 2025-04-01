package com.example.KaizenStream_BE.dto.respone.profile;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private String fullName;


    private String phoneNumber;


    private String address;


    private String bio;

    private String avatarUrl;


    private String gender;

    private Date dateOfBirth;



    private String channelName;


    private String bankAccountNumber;


    private String bankName;

    private String description;

    boolean existsByUser_UserId(String userId) {
        return false;
    }

}
