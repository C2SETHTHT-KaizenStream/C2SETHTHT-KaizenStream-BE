package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.tag.CreateTagRequest;
import com.example.KaizenStream_BE.dto.respone.tag.TagRespone;
import com.example.KaizenStream_BE.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagRespone toTagRespone(Tag tag);

    @Mapping(target = "tagId", ignore = true)
    @Mapping(target = "livestreams", ignore = true)
    Tag toTag(CreateTagRequest tagRequest);
}
