package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.authen.AuthenticationRequest;
import com.example.KaizenStream_BE.dto.request.authen.RegisterRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.authen.AuthenticationResponse;
import com.example.KaizenStream_BE.dto.respone.authen.RegisterResponse;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.SuccessCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.service.AuthenticationService;
import com.example.KaizenStream_BE.service.RegisterService;
import com.example.KaizenStream_BE.service.UserService;
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
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    RegisterService registerService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        System.out.println("Request Body: " + request);

        try {
            // Thực hiện xác thực người dùng
            var result = authenticationService.authenticate(request);

            // Tạo ApiResponse cho kết quả thành công
            ApiResponse<AuthenticationResponse> response = ApiResponse.<AuthenticationResponse>builder()
                    .code(200)  // Mã thành công
                    .message("Authentication successful")
                    .result(result)
                    .build();

            return ResponseEntity.ok(response);

        } catch (AppException e) {
            // Nếu có lỗi (AppException), trả về lỗi và thông báo
            ApiResponse<AuthenticationResponse> errorResponse = ApiResponse.<AuthenticationResponse>builder()
                    .code(401)  // Mã lỗi xác thực
                    .message(e.getMessage())  // Thông báo lỗi từ AppException
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);  // Trả về HTTP 401 Unauthorized
        }

    }



    @PostMapping("/register")
    ApiResponse<RegisterResponse> response(@RequestBody RegisterRequest request)

    {
        var result= registerService.register(request);
        return ApiResponse.<RegisterResponse>builder()
                .result(result).build();
    }
//    public User register(@Valid  @RequestBody RegisterRequest registerRequest){
//
//        return registerService.register(registerRequest );
//        return ResponseEntity.ok(ApiResponse.builder()
//                .code(SuccessCode.REGISTER_SUCCESS.getCode())
//                .message(SuccessCode.REGISTER_SUCCESS.getMessage())
//                .result(registerService.register(registerRequest))
//                .build()

    }


