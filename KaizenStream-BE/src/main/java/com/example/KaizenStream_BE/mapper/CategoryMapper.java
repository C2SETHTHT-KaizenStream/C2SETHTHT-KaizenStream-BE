package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.category.CreateCategoryRequest;
import com.example.KaizenStream_BE.dto.respone.category.CategoryRespone;
import com.example.KaizenStream_BE.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryRespone toCategoryRespone(Category category);
    @Mapping(target = "categoryId", ignore = true)
    Category toCategory(CreateCategoryRequest createCategoryRequest);
}
