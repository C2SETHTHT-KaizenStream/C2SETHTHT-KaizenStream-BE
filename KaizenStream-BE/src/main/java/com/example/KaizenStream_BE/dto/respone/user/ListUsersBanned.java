package com.example.KaizenStream_BE.dto.respone.user;

import com.example.KaizenStream_BE.enums.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListUsersBanned {
    String userId;
    String userName;
    String email;
    AccountStatus status;
    LocalDateTime banUntil;
}
