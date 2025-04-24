package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.profile.FollowRespone;
import com.example.KaizenStream_BE.service.FollowerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins ="${fe-url}", allowedHeaders = "*")
@RequestMapping("/follow")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowerController {
    FollowerService followerService;

    @PostMapping("/{followerId}/follow/{followingId}")
    public ApiResponse<?> follow(@PathVariable String followerId, @PathVariable String  followingId) {
        followerService.follow(followerId, followingId);
        return ApiResponse.<String>builder().result("Success!").build();
    }

    @DeleteMapping("/{followerId}/unfollow/{followingId}")
    public ApiResponse<?> unfollow(@PathVariable String followerId, @PathVariable String followingId) {
        followerService.unfollow(followerId, followingId);
        return ApiResponse.<String>builder().result("Unfollowed successfully!").build();
    }

    //
    @GetMapping("/{userId}/followers")
    public ApiResponse<List<FollowRespone>> getFollowers(@PathVariable String userId) {
        return ApiResponse.<List<FollowRespone>>builder().result(followerService.getFollowers(userId)).build();
    }

    @GetMapping("/{userId}/following")
    public ApiResponse<List<FollowRespone>>getFollowing(@PathVariable String userId) {
        return ApiResponse.<List<FollowRespone>>builder().result(followerService.getFollowing(userId)).build();
    }
}
