package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.profile.CreateProfileRequest;
import com.example.KaizenStream_BE.dto.request.profile.UpdateProfileRequest;
import com.example.KaizenStream_BE.dto.respone.profile.ProfileResponse;
import com.example.KaizenStream_BE.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {

    @Mapping(source = "profileId", target = "profileId")
    ProfileResponse toProfileRespone(Profile profile);

    @Mapping(target = "user", ignore = true) // Set trong service/controller
    Profile toProfile(CreateProfileRequest request);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true) // Specifically ignore avatarUrl to preserve it
    void updateProfile(@MappingTarget Profile profile, UpdateProfileRequest request);
}
