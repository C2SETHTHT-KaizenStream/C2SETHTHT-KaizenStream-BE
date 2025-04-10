package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.respone.suggestion.SuggestionResponse;
import com.example.KaizenStream_BE.entity.Livestream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SuggestionMapper {

    @Mapping(target = "score", ignore = true)
    @Mapping(target = "streamerId", source = "user.userId")
    @Mapping(target = "streamerName", source = "user.userName")

    @Mapping(target = "thumbnailUrl", source = "thumbnail")
    @Mapping(target = "streamUrl", ignore = true)
    @Mapping(target = "viewerCount", source = "viewerCount")
    @Mapping(target = "startTime", expression = "java(livestream.getStartTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())")
    @Mapping(target = "tags",
            expression = "java(convertTagsToStringSet(livestream.getTags().stream().map(t -> t.getName()).collect(java.util.stream.Collectors.toSet())))")

    @Mapping(target = "categories",
            expression = "java(convertCategoriesToStringSet(livestream.getCategories().stream().map(c -> c.getName()).collect(java.util.stream.Collectors.toSet())))")

    @Mapping(target = "endTime", expression = "java(livestream.getEndTime() != null ? livestream.getEndTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null)")
    SuggestionResponse toSuggestionResponse(Livestream livestream);


    @Named("convertTagsToStringSet")
    default Set<String> convertTagsToStringSet(Set<String> tags) {
        return tags != null ? tags : Set.of();
    }

    @Named("convertCategoriesToStringSet")
    default Set<String> convertCategoriesToStringSet(Set<String> categories) {
        return categories != null ? categories : Set.of();
    }
}