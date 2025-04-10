package com.example.KaizenStream_BE.mapper;

import org.mapstruct.Named;

import java.util.Set;

public interface SetConverter {

    @Named("convertTagsToStringSet")
    default Set<String> convertTagsToStringSet(Set<String> tags) {
        return tags != null ? tags : Set.of();
    }

    @Named("convertCategoriesToStringSet")
    default Set<String> convertCategoriesToStringSet(Set<String> categories) {
        return categories != null ? categories : Set.of();
    }
}
