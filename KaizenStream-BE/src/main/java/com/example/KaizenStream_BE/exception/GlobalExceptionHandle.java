//package com.example.KaizenStream_BE.exception;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandle {
//
//    // Bắt lỗi validation
//    @ExceptionHandler(org.springframework.validation.BindException.class)
//    public ResponseEntity<String> handleValidationException(BindingResult bindingResult) {
//        StringBuilder errorMessage = new StringBuilder();
//        bindingResult.getFieldErrors().forEach(error -> {
//            errorMessage.append(error.getField())
//                    .append(": ")
//                    .append(error.getDefaultMessage())
//                    .append("\n");
//        });
//        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
//    }
//
//    // Bắt các lỗi khác, ví dụ AppException (trùng lặp email, username)
//    @ExceptionHandler(AppException.class)
//    public ResponseEntity<String> handleAppException(AppException ex) {
//        log.error("loiiiiiiiiiiiiiiiiii--------------");
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
//    }
//
//    // Bắt các lỗi chung
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGeneralException(Exception ex) {
//        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}
