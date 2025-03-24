package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.category.CreateCategoryRequest;
import com.example.KaizenStream_BE.dto.respone.category.CategoryRespone;
import com.example.KaizenStream_BE.entity.Category;
import com.example.KaizenStream_BE.mapper.CategoryMapper;
import com.example.KaizenStream_BE.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    public String deleteById(String id) {
        var cate=categoryRepository.findByName(id).orElseThrow(()->new RuntimeException("CATEGORY_NOT_EXITS"));
        categoryRepository.deleteById(id);
        return "Category has been deleted";
    }

    public CategoryRespone createCategory(CreateCategoryRequest request) {
       // var cate=categoryRepository.findByName(request.getName()).orElseThrow(()->new RuntimeException("CATEGORY_EXITED"));
        Category category=categoryMapper.toCategory(request);
        categoryRepository.save(category);
        return categoryMapper.toCategoryRespone(category);

    }

    public List<CategoryRespone> getAll() {
        return categoryRepository.findAll().stream().map(ca->categoryMapper.toCategoryRespone(ca)).toList();
    }

    public CategoryRespone getById(String id) {
        return categoryMapper.toCategoryRespone(categoryRepository.findById(id).orElseThrow(()->new RuntimeException("CATEGORY_NOT_EXITS")));
    }
}
