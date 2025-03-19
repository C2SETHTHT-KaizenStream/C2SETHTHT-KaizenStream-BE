package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.authen.AuthenticationRequest;
import com.example.KaizenStream_BE.dto.request.authen.RegisterRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.authen.AuthenticationResponse;
import com.example.KaizenStream_BE.dto.respone.authen.RegisterResponse;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.SuccessCode;
import com.example.KaizenStream_BE.service.AuthenticationService;
import com.example.KaizenStream_BE.service.RegisterService;
import com.example.KaizenStream_BE.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.Request;
import org.apache.logging.log4j.LogManager;
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
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        System.out.println("Request Body: " + request);

        var result=authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result).build();
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


