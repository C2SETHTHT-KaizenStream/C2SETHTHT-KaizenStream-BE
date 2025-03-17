package com.example.KaizenStream_BE.dto.request.authen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String role_name;
    private String userName;
    private String password;
    private int point;
    private String channelName;
    private String bankAccountNumber;
    private String bankName;
    private String description;
    private String status;
    private int followerCount;


}

