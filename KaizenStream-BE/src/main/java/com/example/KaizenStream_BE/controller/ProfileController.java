package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.profile.CreateProfileRequest;
import com.example.KaizenStream_BE.dto.request.profile.UpdateProfileRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.profile.ProfileResponse;
import com.example.KaizenStream_BE.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {
    private final ProfileService profileService;
    private final ObjectMapper objectMapper;

//    @PostMapping("/{userId}")
//    public ResponseEntity<CreateProfileRequest> createProfile(
//            @PathVariable String userId,
//            @RequestBody CreateProfileRequest profileDTO,
//            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) throws IOException {
//        return ResponseEntity.ok(profileService.createProfile(userId, profileDTO, avatarFile));
//    }

    // Endpoint để tạo mới profile
    @PostMapping
    ApiResponse<ProfileResponse> createProfile(@RequestBody @Valid CreateProfileRequest request){
        return ApiResponse.<ProfileResponse>builder().result(profileService.createProfile(request)).build();
    }

    // Endpoint để cập nhật profile (không bao gồm avatar)
    @PutMapping(value = "/{profileId}")
    public ApiResponse<ProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @PathVariable String profileId) {
        
        System.out.println("Received request to update profile for profileId: " + profileId);
        System.out.println("Request object: " + request);
        
        try {
            // Gọi service để cập nhật profile (không bao gồm avatar)
            ProfileResponse response = profileService.updateProfile(profileId, request, null);
            
            return ApiResponse.<ProfileResponse>builder()
                    .message("Profile updated successfully")
                    .result(response)
                    .build();
        } catch (Exception e) {
            System.err.println("Error processing profile update: " + e.getMessage());
            e.printStackTrace();
            
            // Return a more specific error response
            return ApiResponse.<ProfileResponse>builder()
                    .message("Failed to update profile: " + e.getMessage())
                    .build();
        }
    }
    
    // Keep the multipart version as well for backward compatibility
    @PutMapping(value = "/{profileId}/form", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProfileResponse> updateProfileMultipart(
            @RequestParam(value = "request") String updateRequestJson,
            @PathVariable String profileId) throws IOException {
        
        System.out.println("Received multipart request to update profile for profileId: " + profileId);
        System.out.println("Request data: " + updateRequestJson);
        
        try {
            // Convert JSON string to UpdateProfileRequest object
            UpdateProfileRequest request = objectMapper.readValue(updateRequestJson, UpdateProfileRequest.class);
            
            System.out.println("Deserialized request object: " + request);
            
            // Gọi service để cập nhật profile (không bao gồm avatar)
            ProfileResponse response = profileService.updateProfile(profileId, request, null);
            
            return ApiResponse.<ProfileResponse>builder()
                    .message("Profile updated successfully")
                    .result(response)
                    .build();
        } catch (Exception e) {
            System.err.println("Error processing profile update: " + e.getMessage());
            e.printStackTrace();
            
            // Return a more specific error response
            return ApiResponse.<ProfileResponse>builder()
                    .message("Failed to update profile: " + e.getMessage())
                    .build();
        }
    }
    
    // Endpoint chỉ để cập nhật avatar của profile
    @PutMapping(value = "/{profileId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProfileResponse> updateProfileAvatar(
            @RequestParam("avatarFile") MultipartFile avatarFile,
            @PathVariable String profileId) {
        
        System.out.println("Received request to update profile avatar for profileId: " + profileId);
        
        // Debug log for avatar file
        if (avatarFile != null) {
            System.out.println("Received avatar file: " + avatarFile.getOriginalFilename() + 
                              ", size: " + avatarFile.getSize() + 
                              ", content type: " + avatarFile.getContentType());
        } else {
            System.out.println("No avatar file received");
            throw new IllegalArgumentException("Avatar file is required");
        }
        
        try {
            ProfileResponse updatedProfile = profileService.updateProfileAvatar(profileId, avatarFile);
            return ApiResponse.<ProfileResponse>builder()
                    .message("Profile avatar updated successfully")
                    .result(updatedProfile)
                    .build();
        } catch (Exception e) {
            System.err.println("Error updating profile avatar: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @DeleteMapping("/{profileId}")
    public ApiResponse<String> deleteProfile(@PathVariable String profileId) {
        profileService.deleteProfile(profileId);
        return ApiResponse.<String>builder()
                .result("Profile deleted successfully.")
                .build();
    }

    // Endpoint để lấy thông tin profile
    @GetMapping("/{userId}")
    public ApiResponse<ProfileResponse> getProfile(@PathVariable String userId){
        return profileService.getProfileById(userId);
    }
}