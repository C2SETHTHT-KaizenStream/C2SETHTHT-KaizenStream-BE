package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.respone.CommentRespone;
import com.example.KaizenStream_BE.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "user.userName", target = "username")
    @Mapping (source = "user.userId", target = "userId")
    CommentRespone toResponse(Comment comment);
}