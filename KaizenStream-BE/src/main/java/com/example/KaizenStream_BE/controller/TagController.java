package com.example.KaizenStream_BE.controller;


import com.example.KaizenStream_BE.dto.request.tag.CreateTagRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.tag.TagRespone;
import com.example.KaizenStream_BE.mapper.TagMapper;
import com.example.KaizenStream_BE.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/tag")
public class TagController {
    TagService tagService;
    TagMapper tagMapper;

    @PostMapping
    ApiResponse<TagRespone> createTag(@RequestBody  CreateTagRequest request){
        return  ApiResponse.<TagRespone>builder().result(tagService.createTag(request)).build();
    }
    @GetMapping
    ApiResponse<List<TagRespone>> getAll(){
        return  ApiResponse.<List<TagRespone>>builder().result(tagService.getAll()).build();
    }
}
