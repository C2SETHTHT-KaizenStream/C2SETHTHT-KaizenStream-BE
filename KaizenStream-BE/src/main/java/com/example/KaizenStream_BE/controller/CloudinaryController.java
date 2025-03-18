package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/upload")
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }
    // Upload nhiều ảnh
//    @PostMapping("/images")
//    public ResponseEntity<List<String>> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
//        try {
//            List<String> imageUrls = cloudinaryService.uploadMultipleImages(files);
//            return ResponseEntity.ok(imageUrls);
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().body(null);
//        }
//    }
    // Upload nhiều ảnh (tối đa 3 ảnh)
    @PostMapping("/images")
    public ResponseEntity<?> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        if (files.length > 3) {
            return ResponseEntity.badRequest().body("Chỉ được upload tối đa 3 ảnh!");
        }
        try {
            List<String> imageUrls = cloudinaryService.uploadMultipleImages(files);
            return ResponseEntity.ok(imageUrls);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload thất bại: " + e.getMessage());
        }
    }
}
