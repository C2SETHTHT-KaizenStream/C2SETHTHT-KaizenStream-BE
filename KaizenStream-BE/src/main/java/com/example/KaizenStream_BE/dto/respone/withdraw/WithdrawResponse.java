    package com.example.KaizenStream_BE.dto.respone.withdraw;

    import com.example.KaizenStream_BE.enums.WithdrawStatus;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDateTime;

    @Getter
    @Setter
    @Builder
    public class WithdrawResponse {

        private String withdrawId;
        private String userId;
        private int pointsRequested;
        private double usdExpected;
        private String bankName;
        private String bankAccount;
        private String bankHolder; // tên chủ tài khoản ngân hàng
        private WithdrawStatus status;
        private String note;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Integer balance;  // Số điểm trong ví của người dùng
    }
