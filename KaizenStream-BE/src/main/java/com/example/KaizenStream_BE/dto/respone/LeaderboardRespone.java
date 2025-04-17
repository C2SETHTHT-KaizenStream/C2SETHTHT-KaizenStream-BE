package com.example.KaizenStream_BE.dto.respone;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class LeaderboardRespone {
    private String userId;
    private String userName;
    private Integer totalViewers;
    private BigDecimal totalDonations;
    private String type;
    private  String imgUrl;
}
