package com.example.KaizenStream_BE.dto.respone.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponse {

    private String userId;      // userName từ User
    private String userName;    // userName từ User

    private String purchaseId;
    private String type;
    private double amount;
    private LocalDateTime purchaseDate;
    private int pointReceived;

    private Integer balance;    // balance từ Wallet

}
