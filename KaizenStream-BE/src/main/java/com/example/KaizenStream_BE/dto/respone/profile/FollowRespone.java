package com.example.KaizenStream_BE.dto.respone.profile;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class FollowRespone {
    String userId;
    String avaUrl;
    String userName;
    private LocalDateTime followedAt;

}
