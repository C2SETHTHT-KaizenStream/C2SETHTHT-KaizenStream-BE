package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.ProfileDTO;
import com.example.KaizenStream_BE.entity.Profile;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.repository.ProfileRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public ProfileDTO createProfile(String userId, ProfileDTO profileDTO, MultipartFile avatarFile) throws IOException {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

        // Check if profile already exists
        if (profileRepository.existsByUser_UserId(userId)) {
            throw new RuntimeException("User đã có profile, vui lòng sử dụng API update");
        }

        // Validate required fields
        if (profileDTO.getFullName() == null || profileDTO.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Họ tên không được để trống");
        }

        // Upload avatar if provided
        String avatarUrl = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                avatarUrl = cloudinaryService.uploadImage(avatarFile);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
            }
        }

        // Create new profile
        Profile profile = Profile.builder()
                .user(user)
                .fullName(profileDTO.getFullName())
                .phoneNumber(profileDTO.getPhoneNumber())
                .address(profileDTO.getAddress())
                .bio(profileDTO.getBio())
                .avatarUrl(avatarUrl)
                .gender(profileDTO.getGender())
                .dateOfBirth(profileDTO.getDateOfBirth())
                .createdAt(new Date())
                .updatedAt(new Date())
                .status("ACTIVE")
                .build();

        Profile savedProfile = profileRepository.save(profile);
        return convertToDTO(savedProfile);
    }

    public ProfileDTO updateProfile(String userId, ProfileDTO profileDTO, MultipartFile avatarFile) throws IOException {
        // Validate profile exists
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy profile cho user ID: " + userId));

        // Validate required fields
        if (profileDTO.getFullName() == null || profileDTO.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Họ tên không được để trống");
        }

        // Upload new avatar if provided
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String avatarUrl = cloudinaryService.uploadImage(avatarFile);
                profile.setAvatarUrl(avatarUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage());
            }
        }

        // Update profile fields
        profile.setFullName(profileDTO.getFullName());
        profile.setPhoneNumber(profileDTO.getPhoneNumber());
        profile.setAddress(profileDTO.getAddress());
        profile.setBio(profileDTO.getBio());
        profile.setGender(profileDTO.getGender());
        profile.setDateOfBirth(profileDTO.getDateOfBirth());
        profile.setUpdatedAt(new Date());

        Profile updatedProfile = profileRepository.save(profile);
        return convertToDTO(updatedProfile);
    }

    public ProfileDTO getProfile(String userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy profile cho user ID: " + userId));
        return convertToDTO(profile);
    }

    public void deleteProfile(String userId) {
        Profile profile = profileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy profile cho user ID: " + userId));
        profileRepository.delete(profile);
    }

    private ProfileDTO convertToDTO(Profile profile) {
        return ProfileDTO.builder()
                .profileId(profile.getProfileId())
                .userId(profile.getUser().getUserId())
                .fullName(profile.getFullName())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .gender(profile.getGender())
                .dateOfBirth(profile.getDateOfBirth())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .status(profile.getStatus())
                .build();
    }
} 