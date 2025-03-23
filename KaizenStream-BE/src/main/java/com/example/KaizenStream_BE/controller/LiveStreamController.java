package com.example.KaizenStream_BE.controller;

import com.cloudinary.Api;
import com.example.KaizenStream_BE.dto.request.livestream.CreateLivestreamRequest;
import com.example.KaizenStream_BE.dto.request.livestream.UpdateLivestreamRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamRespone;
import com.example.KaizenStream_BE.mapper.LivestreamMapper;
import com.example.KaizenStream_BE.service.LivestreamService;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/livestream")
public class LiveStreamController {
    LivestreamService livestreamService;
    LivestreamMapper livestreamMapper;
    @PostMapping
    ApiResponse<LivestreamRespone> createLivestream(@RequestBody @Valid CreateLivestreamRequest request){
        ApiResponse<LivestreamRespone> response= new ApiResponse<>();
        response.setResult(livestreamMapper.toLivestreamRespone(livestreamService.createLivestream(request)));
        return response;
    }
    @GetMapping
    ApiResponse<List<LivestreamRespone>>  getAll(){
        return ApiResponse.<List<LivestreamRespone>>builder().result(livestreamService.getAll()).build();
    }
    @GetMapping("/{id}")
    ApiResponse<LivestreamRespone>  getLivestreamById(@PathVariable("id") String id){
         return ApiResponse.<LivestreamRespone>builder().result(livestreamService.getLivestreamById(id)).code(200).build();
    }
    @PutMapping()
    ApiResponse<LivestreamRespone> updateById(@RequestBody @Valid UpdateLivestreamRequest updateLivestreamRequest){
        return  ApiResponse.<LivestreamRespone>builder().result(livestreamService.updateLivestreamById(updateLivestreamRequest)).build();
    }
    @DeleteMapping("/{id}")
    ApiResponse<String> deleteById(@PathVariable("id") String id){
        return ApiResponse.<String>builder().result(livestreamService.deleteById(id)).build();
    }



}
