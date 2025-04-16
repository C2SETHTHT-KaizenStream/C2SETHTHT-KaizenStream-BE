package com.example.KaizenStream_BE.enums;

public enum AccountStatus {
    ACTIVE,           // Tài khoản đang hoạt động bình thường
    INACTIVE,         // Tài khoản không hoạt động (do chưa xác minh email hoặc tự tắt)
    SUSPENDED,        // Tạm thời bị khóa (do vi phạm hoặc báo cáo)
    BANNED,           // Bị khóa vĩnh viễn
    DELETED,          // Đã xóa bởi người dùng (soft delete)
    PENDING_VERIFICATION, // Đang chờ xác minh (email/phone...)
    LOCKED,           // Bị khóa do bảo mật (quên mật khẩu, nhập sai nhiều lần)
    DISABLED_BY_ADMIN // Bị vô hiệu hóa bởi admin (xử lý thủ công)
}
