package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.profile.CreateProfileRequest;
import com.example.KaizenStream_BE.dto.request.profile.UpdateProfileRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.profile.ProfileResponse;
import com.example.KaizenStream_BE.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {
    private final ProfileService profileService;

//    @PostMapping("/{userId}")
//    public ResponseEntity<CreateProfileRequest> createProfile(
//            @PathVariable String userId,
//            @RequestBody CreateProfileRequest profileDTO,
//            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) throws IOException {
//        return ResponseEntity.ok(profileService.createProfile(userId, profileDTO, avatarFile));
//    }

    @PostMapping
    ApiResponse<ProfileResponse> createProfile(@RequestBody @Valid CreateProfileRequest request){
        return  ApiResponse.<ProfileResponse>builder().result(profileService.createProfile(request)).build();
    }

    @PutMapping("/{profile_id}")
    ApiResponse<ProfileResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest request , @PathVariable String profile_id )
    {
        return ApiResponse.<ProfileResponse>builder().result(profileService.updateProfile(profile_id , request)).build();
    }

    @DeleteMapping("/{profileId}")
    public ApiResponse<String> deleteProfile(@PathVariable String profileId) {
        profileService.deleteProfile(profileId);
        return ApiResponse.<String>builder()
                .result("Profile deleted successfully.")
                .build();
    }




}