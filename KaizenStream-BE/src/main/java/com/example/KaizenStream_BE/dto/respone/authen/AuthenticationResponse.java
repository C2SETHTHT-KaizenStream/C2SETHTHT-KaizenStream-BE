package com.example.KaizenStream_BE.dto.respone.authen;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;           // access token only
    String userId;

    String userName;
    String role;
    boolean authenticated;

}
