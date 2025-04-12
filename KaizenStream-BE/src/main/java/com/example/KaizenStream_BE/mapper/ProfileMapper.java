package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.profile.CreateProfileRequest;
import com.example.KaizenStream_BE.dto.request.profile.UpdateProfileRequest;
import com.example.KaizenStream_BE.dto.respone.profile.ProfileResponse;
import com.example.KaizenStream_BE.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(source = "profileId", target = "profileId")
    ProfileResponse toProfileRespone(Profile profile);

    @Mapping(target = "user", ignore = true) // Set trong service/controller
    Profile toProfile(CreateProfileRequest request);

    @Mapping(target = "user", ignore = true)
    void updateProfile(@MappingTarget Profile profile, UpdateProfileRequest request);
}
