package com.example.KaizenStream_BE.service;

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

        // Tạo mới profile
        Profile profile = profileMapper.toProfile(request);
        profile.setUser(user);

        // Lưu profile vào cơ sở dữ liệu
        profileRepository.save(profile);

        return profileMapper.toProfileRespone(profile); // Trả về thông tin profile đã được tạo
    }

    // Cập nhật profile + channel_name trong user
    public ProfileResponse updateProfile(String profileId, @Valid UpdateProfileRequest updateProfileRequest) {

        // Lấy thông tin profile hiện tại
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILES_NOT_EXIST));

        // Cập nhật profile
        profileMapper.updateProfile(profile, updateProfileRequest);
        profileRepository.save(profile);

        // Cập nhật thông tin channelName trong bảng users
        User user = profile.getUser();
        user.setChannelName(updateProfileRequest.getChannelName());
        userRepository.save(user); // Cập nhật thông tin user

        return profileMapper.toProfileRespone(profile); // Trả về thông tin profile đã được cập nhật
    }

    public void deleteProfile(String profileId) {
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILES_NOT_EXIST));

        profileRepository.delete(profile);
    }


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

        // Thêm channelName và userName từ User vào response
        response.setChannelName(user.getChannelName());
        response.setUserName(user.getUserName());

        // Trả về ApiResponse
        return ApiResponse.<ProfileResponse>builder()
                .message("Profile fetched successfully")
                .result(response)
                .build();
    }
}
