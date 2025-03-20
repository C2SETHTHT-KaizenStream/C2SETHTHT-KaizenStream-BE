package com.example.KaizenStream_BE.dto.request.authen;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)


public class UserCreationRequest {

    @Getter
    @Size(min = 3, message = "USERNAME_INVALID")
    private String username;

    @Size(min = 8, message = "PASSWORD_INVALID")
    private String password;

    @Getter
    @Email(message = "INVALID_EMAIL")
    private String email;

    private String firstname;
    private String lastname;
}


