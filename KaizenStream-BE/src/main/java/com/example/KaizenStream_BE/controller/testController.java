package com.example.KaizenStream_BE.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

@RequestMapping("/testApi")
public class testController {
    @GetMapping
    String getUsers(){
        return "Test success!!";
    }

}
