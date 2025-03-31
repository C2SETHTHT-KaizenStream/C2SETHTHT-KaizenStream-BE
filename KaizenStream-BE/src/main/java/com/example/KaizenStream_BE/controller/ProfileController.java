package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.ProfileDTO;
import com.example.KaizenStream_BE.service.ProfileService;
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

    @PostMapping("/{userId}")
    public ResponseEntity<ProfileDTO> createProfile(
            @PathVariable String userId,
            @RequestBody ProfileDTO profileDTO,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) throws IOException {
        return ResponseEntity.ok(profileService.createProfile(userId, profileDTO, avatarFile));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ProfileDTO> updateProfile(
            @PathVariable String userId,
            @RequestBody ProfileDTO profileDTO,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) throws IOException {
        return ResponseEntity.ok(profileService.updateProfile(userId, profileDTO, avatarFile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String userId) {
        profileService.deleteProfile(userId);
        return ResponseEntity.ok().build();
    }
} 