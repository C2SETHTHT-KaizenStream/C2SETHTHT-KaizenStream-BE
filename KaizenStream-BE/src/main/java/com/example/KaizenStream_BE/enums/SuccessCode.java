package com.example.KaizenStream_BE.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
public enum SuccessCode {
    REGISTER_SUCCESS(20000,"Register success", HttpStatus.CREATED)
    ;
    private  int code;
    private  String message;
    private HttpStatusCode statusCode;
}
