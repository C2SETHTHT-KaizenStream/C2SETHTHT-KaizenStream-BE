package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.LeaderboardRespone;
import com.example.KaizenStream_BE.entity.Leaderboard;
import com.example.KaizenStream_BE.service.LeaderboardInitService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LeaderboardController {

    LeaderboardInitService leaderboardInitService;

    @PostMapping("/init")
    public ApiResponse<String> initLeaderboard() {
        leaderboardInitService.initializeLeaderboardForAllUsers();
        return ApiResponse.<String>builder().result("Leaderboard initialized for all users.").build();
    }
    @GetMapping("/top")
    public ApiResponse<List<LeaderboardRespone>> getTop20Leaderboard(@RequestParam String type) {
        return ApiResponse.<List<LeaderboardRespone>>builder()
                .result(leaderboardInitService.getTop20ByType(type))
                .build();
    }

}
