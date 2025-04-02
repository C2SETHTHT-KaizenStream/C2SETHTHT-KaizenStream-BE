package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.authen.RegisterRequest;
import com.example.KaizenStream_BE.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Chỉ ánh xạ 3 trường userName, password, email
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "email", source = "email")
    // Bỏ qua tất cả các trường khác
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "followerCount", ignore = true)
    @Mapping(target = "point", ignore = true)
    @Mapping(target = "channelName", ignore = true)

    @Mapping(target = "status", ignore = true)

    User toUser(RegisterRequest registerRequest);
}