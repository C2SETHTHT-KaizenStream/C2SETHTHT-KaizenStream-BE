package com.example.KaizenStream_BE.dto.request.withdraw;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWithdrawRequest {

    private int pointsRequested;
    private String bankName;
    private String bankAccount;

}
