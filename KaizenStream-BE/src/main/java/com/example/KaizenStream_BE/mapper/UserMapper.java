package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.authen.RegisterRequest;
import com.example.KaizenStream_BE.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

//    @Mapping(source = "roles.name",target = "role_name")
    User toUser (RegisterRequest registerRequest);

}
