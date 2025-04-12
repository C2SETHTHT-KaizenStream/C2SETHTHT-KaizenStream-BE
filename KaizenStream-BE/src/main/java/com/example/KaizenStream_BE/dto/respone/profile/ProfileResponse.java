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

    private String profileId;
    private String fullName;
    private String channelName;  // channelName từ User
    private String userName;     // userName từ User
    private String phoneNumber;
    private String address;
    private String bio;
    private String avatarUrl;
    private String gender;
    private Date dateOfBirth;
    private String bankAccountNumber;
    private String bankName;
    private String description;
    private Integer balance;    // balance từ Wallet
    boolean existsByUser_UserId(String userId) {
        return false;
    }

}
