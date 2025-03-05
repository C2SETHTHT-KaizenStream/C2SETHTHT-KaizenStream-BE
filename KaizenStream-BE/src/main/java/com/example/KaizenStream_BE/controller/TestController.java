package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.test.TestRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.entity.Test;
import com.example.KaizenStream_BE.service.TestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/testApi")
public class TestController {
    TestService testService;
    @GetMapping
    String getUsers() {
        return "Test success!!";
    }

    @PostMapping
    ApiResponse<Test>createTest(@RequestBody TestRequest request){
        ApiResponse<Test> response=new ApiResponse<>();
        response.setResult(testService.createTest(request));
        return response;
    }


}
