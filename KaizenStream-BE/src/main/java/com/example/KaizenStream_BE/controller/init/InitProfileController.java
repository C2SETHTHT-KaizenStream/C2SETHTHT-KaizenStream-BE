package com.example.KaizenStream_BE.controller.init;

import com.example.KaizenStream_BE.service.init.InitProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/init")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InitProfileController {

    private final InitProfileService initProfileService;

    @PostMapping("/profile")
    public String initProfileForAllUser() {
        int createdCount = initProfileService.initProfileForAllUser();
        return "Init profile success for " + createdCount + " users.";
    }
}
