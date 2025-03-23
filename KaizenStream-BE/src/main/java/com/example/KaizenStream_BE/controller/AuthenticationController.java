package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.authen.AuthenticationRequest;
import com.example.KaizenStream_BE.dto.request.authen.RegisterRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.authen.AuthenticationResponse;
import com.example.KaizenStream_BE.dto.respone.authen.LogoutResponse;
import com.example.KaizenStream_BE.dto.respone.authen.RegisterResponse;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.enums.SuccessCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.service.AuthenticationService;
import com.example.KaizenStream_BE.service.RegisterService;
import com.example.KaizenStream_BE.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.Request;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    RegisterService registerService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {
        try {
            var result = authenticationService.authenticate(request, response);
            
            return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder()
                    .code(200)
                    .message("Login successful")
                    .result(result)
                    .build());
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<AuthenticationResponse>builder()
                            .code(e.getErrorCode().getCode())
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<AuthenticationResponse>builder()
                            .code(ErrorCode.UNAUTHENTICATED.getCode())
                            .message("Refresh token not found")
                            .build());
        }

        try {
            AuthenticationResponse response = authenticationService.refreshAccessToken(refreshToken);
            
            return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder()
                    .code(200)
                    .message("SuccessCode.TOKEN_REFRESHED.getMessage()")
                    .result(response)
                    .build());
                    
        } catch (AppException e) {
            return ResponseEntity.status(e.getErrorCode().getStatusCode())
                    .body(ApiResponse.<AuthenticationResponse>builder()
                            .code(e.getErrorCode().getCode())
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/logout") 
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(HttpServletResponse response) {
        authenticationService.logout(response);
        
        LogoutResponse logoutResponse = LogoutResponse.builder()
                .loggedOut(true)
                .message("SuccessCode.LOGOUT_SUCCESS.getMessage()")
                .build();

        return ResponseEntity.ok(ApiResponse.<LogoutResponse>builder()
                .code(200)
                .message("SuccessCode.LOGOUT_SUCCESS.getMessage()")
                .result(logoutResponse)
                .build());
    }

    @PostMapping("/register")
    ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest request) {
        var result = registerService.register(request);
        return ApiResponse.<RegisterResponse>builder()
                .result(result)
                .build();
    }
}


