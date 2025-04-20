package com.example.KaizenStream_BE.service;

import com.cloudinary.Cloudinary;
import com.example.KaizenStream_BE.dto.request.profile.CreateProfileRequest;
import com.example.KaizenStream_BE.dto.request.profile.UpdateProfileRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.profile.ProfileResponse;
import com.example.KaizenStream_BE.entity.Profile;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.ProfileMapper;
import com.example.KaizenStream_BE.repository.ProfileRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import com.example.KaizenStream_BE.repository.WalletRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final ProfileMapper profileMapper;
    private final WalletRepository walletRepository;

    // Tạo mới profile + cập nhập channel_name trong user
    public ProfileResponse createProfile(CreateProfileRequest request) {
        // Check nếu user đã có profile → báo lỗi
        if (profileRepository.existsByUser_UserId(request.getUserId())) {
            throw new AppException(ErrorCode.USER_ALREADY_HAS_PROFILE);
        }

        // Lấy user từ userId
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Cập nhật channel_name trong bảng users
        user.setChannelName(request.getChannelName());
        userRepository.save(user); // Lưu lại user với channelName mới

        // Tạo mới profile với avatarUrl mặc định
        Profile profile = profileMapper.toProfile(request);
        profile.setUser(user);
        profile.setAvatarUrl("https://api.dicebear.com/9.x/adventurer/svg?seed=Amaya"); // Avatar mặc định

        // Lưu profile vào cơ sở dữ liệu
        profileRepository.save(profile);

        // Tạo response với ProfileResponse
        ProfileResponse response = profileMapper.toProfileRespone(profile);

        // Manually set profileId if it's not being mapped correctly
        if (response.getProfileId() == null) {
            response.setProfileId(profile.getProfileId());
        }

        return response; // Trả về thông tin profile đã được tạo
    }


    // Cập nhật profile + channel_name trong user
    public ProfileResponse updateProfile(String profileId, @Valid UpdateProfileRequest updateProfileRequest, MultipartFile avatarFile) {
        try {
            // Log the update request safely
            System.out.println("Update request for profileId: " + profileId);
            System.out.println("Request details: channelName=" + updateProfileRequest.getChannelName() 
                + ", gender=" + updateProfileRequest.getGender()
                + ", dateOfBirth=" + updateProfileRequest.getDateOfBirth()
                + ", phoneNumber=" + updateProfileRequest.getPhoneNumber());
            
            // Lấy thông tin profile hiện tại
            var profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new AppException(ErrorCode.PROFILES_NOT_EXIST));
    
            // Log basic profile info before update
            System.out.println("Profile before update: id=" + profile.getProfileId() 
                + ", avatarUrl=" + profile.getAvatarUrl()
                + ", gender=" + profile.getGender());
            
            // Cập nhật profile
            profileMapper.updateProfile(profile, updateProfileRequest);
    
            // Log basic profile info after update
            System.out.println("Profile after update: id=" + profile.getProfileId() 
                + ", avatarUrl=" + profile.getAvatarUrl()
                + ", gender=" + profile.getGender());
            
            // Lưu thông tin profile đã cập nhật
            profileRepository.save(profile);
            System.out.println("Profile updated successfully in database");
    
            // Cập nhật thông tin channelName trong bảng users
            if (profile.getUser() != null && updateProfileRequest.getChannelName() != null) {
                User user = profile.getUser();
                user.setChannelName(updateProfileRequest.getChannelName());
                userRepository.save(user); // Cập nhật thông tin user
                System.out.println("User channelName updated to: " + updateProfileRequest.getChannelName());
            }
    
            // Tạo response với ProfileResponse
            ProfileResponse response = profileMapper.toProfileRespone(profile);
            
            // Đảm bảo rằng profileId được gán đúng
            if (response.getProfileId() == null) {
                response.setProfileId(profile.getProfileId());
            }
            
            System.out.println("Returning profile response with profileId: " + response.getProfileId());
    
            return response; // Trả về thông tin profile đã được cập nhật
        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Thêm phương thức mới chỉ dành cho cập nhật avatar
    public ProfileResponse updateProfileAvatar(String profileId, MultipartFile avatarFile) {
        // Lấy thông tin profile hiện tại
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILES_NOT_EXIST));

        // Kiểm tra và upload avatar
        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Kiểm tra loại file
            String contentType = avatarFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File phải là định dạng ảnh (JPEG, PNG, v.v.)");
            }

            try {
                System.out.println("Attempting to upload avatar file: " + avatarFile.getOriginalFilename() 
                    + ", size: " + avatarFile.getSize() 
                    + ", content type: " + avatarFile.getContentType());
                
                // Sử dụng CloudinaryService để upload ảnh
                String avatarUrl = cloudinaryService.uploadImage(avatarFile);
                
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    System.out.println("Successfully uploaded to Cloudinary: " + avatarUrl);
                    profile.setAvatarUrl(avatarUrl);  // Cập nhật avatarUrl trong profile
                } else {
                    System.err.println("Failed to get URL from Cloudinary");
                }
            } catch (IOException e) {
                System.err.println("Error uploading avatar to Cloudinary: " + e.getMessage());
                e.printStackTrace();
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        } else {
            throw new IllegalArgumentException("Avatar file is required");
        }

        // Cập nhật avatarImg trong bảng User sau khi avatarUrl ở bảng Profile đã được cập nhật
        User user = profile.getUser();  // Lấy thông tin User từ Profile
        if (user != null) {
            user.setAvatarImg(profile.getAvatarUrl());  // Cập nhật avatarImg trong User
            userRepository.save(user);  // Lưu thông tin User đã cập nhật
            System.out.println("User avatarImg also updated to: " + profile.getAvatarUrl());
        }

        // Lưu thông tin profile đã cập nhật
        profileRepository.save(profile);
        System.out.println("Profile avatar updated, new URL: " + profile.getAvatarUrl());

        // Tạo response với ProfileResponse
        ProfileResponse response = profileMapper.toProfileRespone(profile);
        
        // Đảm bảo rằng profileId được gán đúng
        if (response.getProfileId() == null) {
            response.setProfileId(profile.getProfileId());
        }

        return response; // Trả về thông tin profile đã được cập nhật
    }

    // Thêm phương thức để đồng bộ avatarUrl trong Profile với avatarImg trong User
    @Transactional
    public void initAvatarForUsers() {
        // Lấy tất cả các Profile
        List<Profile> profiles = profileRepository.findAll();

        for (Profile profile : profiles) {
            // Lấy User liên kết với Profile
            User user = profile.getUser();
            if (user != null) {
                String avatarUrl = profile.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    // Cập nhật avatarImg của User
                    user.setAvatarImg(avatarUrl);  // Đồng bộ avatarImg với avatarUrl
                    userRepository.save(user);  // Lưu thông tin User đã được cập nhật
                    System.out.println("Updated avatarImg for user: " + user.getUserName());
                }
            }
        }
    }

    // Phương thức delete Profile
    public void deleteProfile(String profileId) {
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILES_NOT_EXIST));

        profileRepository.delete(profile);
    }

    // Get profileId by UserId
    public ApiResponse<ProfileResponse> getProfileById(String id) {
        // Tìm user theo userId
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Tìm wallet của user
        var walletOfUser = walletRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXIST));

        // Tìm thông tin profile của user
        var profile = profileRepository.findByUser_UserId(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILES_NOT_EXIST));

        // Chuyển đổi profile thành ProfileResponse thông qua profileMapper
        var response = profileMapper.toProfileRespone(profile);

        // Thêm balance từ wallet vào response
        response.setBalance(walletOfUser.getBalance());

        // Thêm channelName,followerCount và userName từ User vào response
        response.setChannelName(user.getChannelName());
        response.setUserName(user.getUserName());
        response.setFollowerCount(user.getFollowerCount());
        
        // Manually set profileId if it's not being mapped correctly
        if (response.getProfileId() == null) {
            response.setProfileId(profile.getProfileId());
        }

        // Trả về ApiResponse
        return ApiResponse.<ProfileResponse>builder()
                .message("Profile fetched successfully")
                .result(response)
                .build();
    }
    
}
