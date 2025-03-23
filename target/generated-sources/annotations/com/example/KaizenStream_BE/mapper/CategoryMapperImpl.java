package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.category.CreateCategoryRequest;
import com.example.KaizenStream_BE.dto.respone.category.CategoryRespone;
import com.example.KaizenStream_BE.entity.Category;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryRespone toCategoryRespone(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryRespone.CategoryResponeBuilder categoryRespone = CategoryRespone.builder();

        categoryRespone.categoryId( category.getCategoryId() );
        categoryRespone.name( category.getName() );
        categoryRespone.description( category.getDescription() );

        return categoryRespone.build();
    }

    @Override
    public Category toCategory(CreateCategoryRequest createCategoryRequest) {
        if ( createCategoryRequest == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( createCategoryRequest.getName() );
        category.setDescription( createCategoryRequest.getDescription() );

        return category;
    }
}
