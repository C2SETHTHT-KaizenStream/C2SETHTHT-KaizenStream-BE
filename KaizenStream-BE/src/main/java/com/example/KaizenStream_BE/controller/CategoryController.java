package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.request.category.CreateCategoryRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.category.CategoryRespone;
import com.example.KaizenStream_BE.mapper.CategoryMapper;
import com.example.KaizenStream_BE.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/category")
public class CategoryController {
    CategoryService categoryService;
    CategoryMapper categoryMapper;
    @PostMapping
    ApiResponse<CategoryRespone> createCategory(@RequestBody CreateCategoryRequest request){
        return  ApiResponse.<CategoryRespone>builder().result(categoryService.createCategory(request)).build();
    }
    @GetMapping
    ApiResponse<List<CategoryRespone>> getAll(){
        return ApiResponse.<List<CategoryRespone>>builder().result(categoryService.getAll()).build();

    }
    @GetMapping("/{id}")
    ApiResponse<CategoryRespone> getById(@PathVariable ("id") String id){
        return ApiResponse.<CategoryRespone>builder().result(categoryService.getById(id)).build();

    }
    @DeleteMapping("/{id}")
    ApiResponse<String> deleteById(@PathVariable("id") String id){
        return ApiResponse.<String>builder().result(categoryService.deleteById(id)).build();
    }

}
