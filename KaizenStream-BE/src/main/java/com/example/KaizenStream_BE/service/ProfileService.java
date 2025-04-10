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

    public ProfileResponse createProfile(CreateProfileRequest request) {
        // Check nếu user đã có profile → báo lỗi
        if (profileRepository.existsByUser_UserId(request.getUserId())) {
            throw new AppException(ErrorCode.USER_ALREADY_HAS_PROFILE);
        }

        // Tìm user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Tạo profile mới
        Profile profile = profileMapper.toProfile(request);
        profile.setUser(user);

        // Lưu và trả về
        profileRepository.save(profile);
        return profileMapper.toProfileRespone(profile);
    }


    public ProfileResponse updateProfile(String profileId, @Valid UpdateProfileRequest updateProfileRequest) {

        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILES_NOT_EXIST));

        // Cập nhật profile
        profileMapper.updateProfile(profile, updateProfileRequest);
        profileRepository.save(profile);

        return profileMapper.toProfileRespone(profile);
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
